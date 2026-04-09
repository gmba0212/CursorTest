# EAI Message Common Module

Spring Boot 기반 메시지 발송 공통 모듈입니다.
메일(EMAIL), 문자(SMS), 알림톡(KTALK)을 공통 구조로 처리합니다.

## 핵심 구조

- 진입 서비스: `TalkService` → 내부에서 `MessageSendService`로 위임
- 요청 DTO: `TalkRequest` (최소 공통 필드 + `params`)
- **선택(조회)**: `MessageDataResolverRegistry` — `messageType`마다 `MessageDataResolver`가 있고, Spring이 주입한 리스트로 맵을 구성한 뒤 `require(messageType)`으로 고릅니다.
- **선택(문구)**: `MessageContentBuilderRegistry` — `messageType`마다 `MessageContentBuilder` → `MessageContent` 생성
- **선택(포맷)**: `ChannelRendererRegistry` — `channelType`마다 `ChannelMessageRenderer` → 채널별 고정길이 Body 문자열 생성
- 헤더: `EaiHeaderFactory` (기존 고정길이 헤더 포맷)
- 전송: `EaiHttpClient`

> 과거의 `MessageBuilderFactory` + 채널별 `*MessageBuilder` 한 덩어리 구조는 제거되었습니다. 대신 **타입별 데이터(resolver)**, **타입별 문구(content builder)**, **채널별 렌더러(renderer)** 가 분리되어 있으며, 각각이 `@Component`로 등록되고 **레지스트리가 EnumMap으로 “어떤 구현을 쓸지”만 고릅니다** (애플리케이션 코드에 `switch`로 타입 나열하지 않음).

## 전체 호출 순서 (팩토리/빌드 선택 포함)

아래는 `talkService.send(request)` 한 번이 내부에서 거치는 순서입니다.

1. **호출부**에서 `TalkRequest` 생성 (`channelType`, `messageType`, 수신자, `params` 등).
2. **`TalkService.send(request)`**  
   - 입력 검증은 `MessageSendService`에서 다시 수행.
3. **`MessageSendService.send(request)`** 시작.
4. **`MessageDataResolverRegistry.require(messageType)`**  
   - 생성자 시점에 Spring이 주입한 `List<MessageDataResolver>`를 순회하며 `supportedType()` → `EnumMap`에 등록해 둠.  
   - `require`는 맵에서 해당 타입의 resolver 한 개를 꺼냄 (없으면 예외).
5. **`MessageDataResolver.resolve(request)`** → **`ServiceData`**  
   - 외부/도메인 서비스(`OrderInfoService`, `AuthService` 등) 호출 및 맵 조립.
6. **`MessageContentBuilderRegistry.require(messageType)`**  
   - 동일하게 `List<MessageContentBuilder>`로 맵 구성 후 `require`.
7. **`MessageContentBuilder.build(request, serviceData)`** → **`MessageContent`**  
   - 채널과 무관한 템플릿 코드, 제목, 본문 등.
8. **`ChannelRendererRegistry.require(channelType)`**  
   - `List<ChannelMessageRenderer>`로 맵 구성 후 `require`.
9. **`ChannelMessageRenderer.renderBody(request, serviceData, content)`** → **Body 문자열**  
   - 예: `KTalkRenderer` → `ATalkBodySendData`, `SmsRenderer` → `SmsBodyPayload` 등.
10. **`EaiHeaderFactory.createHeader(...)`**  
    - 트랜잭션 ID, `channelType`, `messageType`, body UTF-8 길이 등으로 헤더 문자열 생성.
11. **`payload = header + body`**
12. **`EaiHttpClient.send(HttpSendRequest)`** — 외부 EAI URL로 `text/plain; charset=UTF-8` POST.

```text
TalkRequest
  -> TalkService.send
       -> MessageSendService.send
            -> MessageDataResolverRegistry.require(messageType)
                 -> MessageDataResolver.resolve  -> ServiceData
            -> MessageContentBuilderRegistry.require(messageType)
                 -> MessageContentBuilder.build -> MessageContent
            -> ChannelRendererRegistry.require(channelType)
                 -> ChannelMessageRenderer.renderBody -> body String
            -> EaiHeaderFactory.createHeader -> header String
            -> payload = header + body
            -> EaiHttpClient.send
```

## “팩토리에서 빌드 고르는 방식” 요약

- **팩토리 클래스**는 `MessageDataResolverRegistry`, `MessageContentBuilderRegistry`, `ChannelRendererRegistry` 세 가지입니다.
- 각 레지스트리는 **`@Component` 생성자**에서 `List<...>`를 받아 `EnumMap`을 채웁니다.
- 런타임 선택은 **`require(enum)` 한 번**이며, 조건 분기용 `switch (messageType)`은 레지스트리 밖의 중앙 서비스에 두지 않습니다.
- 새 메시지 타입을 넣을 때는 보통 **enum 상수 + Resolver `@Component` 1개 + ContentBuilder `@Component` 1개**를 추가하면 되고, 기존 레지스트리 소스는 수정하지 않습니다.

## 전문/전송 원칙

- 전문은 문자열 기반 `[EAI Header] + [채널별 Body]`
- JSON 직렬화 전송 미사용
- `Content-Type: text/plain; charset=UTF-8`

```java
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

## 헤더 재사용

`EaiHeaderGenerator` 고정길이 패딩을 `EaiHeaderFactory`에서 호출하고, `MessageSendService`가 body 길이와 함께 헤더를 만듭니다.

## 샘플 실행

`SampleTalkRunner`가 샘플 요청을 생성해 `TalkService`를 호출합니다.

기본 전송 URL: `http://localhost:8081/eai/send`

```properties
eai.endpoint=http://your-eai-host/eai/send
sample.run=true
```

더 자세한 설계·패키지·예외 기준은 `README2.md`를 참고하세요.
