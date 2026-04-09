# EAI Message Common Module

Spring Boot 기반 메시지 발송 공통 모듈입니다.  
메일(EMAIL), 문자(SMS), 알림톡(KTALK)을 공통 구조로 처리합니다.

## 목표

업무 코드에서는 아래 한 줄로 발송을 요청한다.

```java
talkService.send(request);
```

모듈이 담당하는 일:

1. 전문 생성 (고정길이 문자열: Header + Body)  
2. 중간 데이터 조회 (메시지 타입별 `MessageDataResolver`)  
3. HTTP 전송 및 응답 코드 검증  

---

## 핵심 구조

- 진입: `TalkService` → `MessageSendService`로 위임  
- 요청 DTO: `TalkRequest` (최소 공통 필드 + `params`)  
- **데이터 조회 선택**: `MessageDataResolverRegistry` — `messageType` → `MessageDataResolver` → `ServiceData`  
- **문구 생성 선택**: `MessageContentBuilderRegistry` — `messageType` → `MessageContentBuilder` → `MessageContent`  
- **채널 포맷 선택**: `ChannelRendererRegistry` — `channelType` → `ChannelMessageRenderer` → Body 문자열  
- 헤더: `EaiHeaderFactory`  
- 전송: `EaiHttpClient`  

과거의 `MessageBuilderFactory` + 채널별 `*MessageBuilder` 한 덩어리 구조는 제거되었다. 지금은 **타입별 데이터(resolver)**, **타입별 문구(content builder)**, **채널별 렌더러(renderer)** 가 분리되고, 구현체는 `@Component`로 등록되며 **레지스트리가 `EnumMap`으로 구현만 고른다**. 중앙 서비스에 `switch (messageType)`으로 나열하지 않는다.

### 채널·메시지 타입은 `params`가 아닌 요청 필드만 사용

`TalkRequest.channelType`, `TalkRequest.messageType`으로만 발송 채널과 메시지 종류를 구분한다.  
`MessageSendService`와 세 Registry는 **`request.getChannelType()` / `request.getMessageType()`만**으로 구현을 고르며, **`params` 안의 키로 채널/메시지 타입을 찾지 않는다.**

`params`는 예를 들어 `orderNo`, `url`, `approverName`, 알림톡 `senderKey`·`templateCode` 같은 **업무 데이터**만 두면 된다. (SMS 전문 고정필드용 코드 등도 `params`의 가짜 “채널” 키로 두지 않으며, 필요 시 요청 DTO에 전용 필드를 추가하는 방향을 권장한다.)

---

## 전체 호출 순서

`talkService.send(request)` 한 번의 내부 진행 순서이다.

### 단계 요약 (번호 순)

1. 호출부에서 `TalkRequest` 생성 (`channelType`, `messageType`, 수신자, `params` 등).  
2. `TalkService.send(request)` → `MessageSendService.send(request)`.  
3. `MessageSendService`에서 `channelType` / `messageType` null 검증.  
4. `MessageDataResolverRegistry.require(messageType)` → `MessageDataResolver.resolve(request)` → **`ServiceData`**.  
5. `MessageContentBuilderRegistry.require(messageType)` → `MessageContentBuilder.build(request, serviceData)` → **`MessageContent`**.  
6. `ChannelRendererRegistry.require(channelType)` → `ChannelMessageRenderer.renderBody(...)` → **Body 문자열**.  
7. `EaiHeaderFactory.createHeader(...)` (body UTF-8 길이 포함) → **Header 문자열**.  
8. `payload = header + body`.  
9. `EaiHttpClient.send(HttpSendRequest)` — `text/plain; charset=UTF-8` POST.  

### 순서도 (상세)

```text
호출부 (TalkRequest 생성)
  -> TalkService.send(request)
       -> MessageSendService.send(request)
            [1] request 검증 (channelType, messageType null 불가)

            [2] 데이터 조회 — messageType 기준
            -> MessageDataResolverRegistry.require(messageType)
                 * 생성자에서 List<MessageDataResolver> 순회
                   supportedType() -> EnumMap<MessageType, Resolver>
                 * require는 맵 조회만 (중앙 switch 없음)
            -> MessageDataResolver.resolve(request) -> ServiceData

            [3] 문구 생성 — messageType 기준
            -> MessageContentBuilderRegistry.require(messageType)
                 * List<MessageContentBuilder> -> EnumMap (동일 패턴)
            -> MessageContentBuilder.build(request, serviceData) -> MessageContent

            [4] 채널 포맷 — channelType 기준
            -> ChannelRendererRegistry.require(channelType)
                 * List<ChannelMessageRenderer> -> EnumMap
            -> ChannelMessageRenderer.renderBody(request, serviceData, content)
                 -> body (ATalk / SMS / Email 고정길이 전문)

            [5] 헤더 + 전송
            -> EaiHeaderFactory.createHeader(...)
            -> payload = header + body
            -> EaiHttpClient.send
```

### 간략 한 줄 트리

```text
TalkRequest
  -> TalkService.send
       -> MessageSendService.send
            -> MessageDataResolverRegistry.require(messageType)
                 -> MessageDataResolver.resolve -> ServiceData
            -> MessageContentBuilderRegistry.require(messageType)
                 -> MessageContentBuilder.build -> MessageContent
            -> ChannelRendererRegistry.require(channelType)
                 -> ChannelMessageRenderer.renderBody -> body String
            -> EaiHeaderFactory.createHeader -> header String
            -> payload = header + body
            -> EaiHttpClient.send
```

### 레지스트리가 “빌드/구현을 고르는” 방식

과거 `MessageBuilderFactory`는 `supports(channelType, messageType)`로 **거대 Builder 하나**를 골랐다.  
지금은 책임을 나누어 **레지스트리 세 종**이 각각 한 차원만 고른다.

| 단계 | 역할 | 레지스트리 | 선택 키 | 구현 예 |
|------|------|------------|---------|---------|
| 데이터 | 외부 데이터 수집 | `MessageDataResolverRegistry` | `MessageType` | `ApprovalRequestDataResolver`, `ShortUrlDataResolver`, … |
| 문구 | 제목·본문·템플릿 코드 | `MessageContentBuilderRegistry` | `MessageType` | `ApprovalRequestContentBuilder`, … |
| 포맷 | 고정길이 Body 조립 | `ChannelRendererRegistry` | `ChannelType` | `KTalkRenderer`, `SmsRenderer`, `EmailRenderer` |

- Spring이 각 구현체를 `@Component`로 스캔한다.  
- 각 Registry 생성자는 `List<해당 인터페이스>`를 받아 기동 시 한 번 `EnumMap`을 만든다.  
- 동일 `supportedType()` / `channelType()`이 두 번이면 생성자에서 `IllegalStateException` (잘못된 중복 등록 방지).  
- 런타임에는 `require(enum)`만 호출한다. `MessageSendService`에 `switch (messageType)` 분기를 두지 않는다.  

### Registry 공통 패턴 (의사코드)

```java
for (Each impl : injectedList) {
    if (map.put(impl.key(), impl) != null) throw duplicate;
}
```

`MessageSendService`는 세 Registry의 `require`만 호출한다.

---

## 인터페이스 요약

**MessageDataResolver**

```java
MessageType supportedType();
ServiceData resolve(TalkRequest request);
```

**MessageContentBuilder**

```java
MessageType supportedType();
MessageContent build(TalkRequest request, ServiceData serviceData);
```

**ChannelMessageRenderer**

```java
ChannelType channelType();
String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content);
```

Renderer는 `MessageType`을 분기하지 않고 `MessageContent`와 수신자 정보만으로 Body를 만든다.

---

## DTO / Enum (최소 구조)

**TalkRequest** (필드 개념):

- `channelType`, `messageType` — **반드시 필드로 설정** (레지스트리 분기 전용, `params`와 무관)  
- `title`, `receiverType`, `receiverAddress`, `receiverId`, `content`, `params`  
- 업무 확장 값은 원칙적으로 `params` (또는 추후 DTO 전용 필드 추가).  
- Resolver가 `ServiceData`를 채우고, ContentBuilder가 `request` + `ServiceData`로 `MessageContent`를 만든다.  

**Enum**

- `ChannelType`: `KTALK`, `EMAIL`, `SMS`  
- `MessageType`: `APPROVAL_REQUEST`, `APPROVAL_COMPLETE`, `SHORT_URL`, `AUTH_CODE`, `SIMPLE_NOTICE`, `SETTLEMENT_RESULT`  

**MessageContent**  
채널과 무관한 템플릿 코드·제목·본문 등의 스냅샷. Renderer는 타입을 몰라도 된다.

---

## 외부 서비스 연동

Resolver 안에서 도메인/외부 서비스를 호출한다. 타입마다 Resolver 클래스를 두어 **중앙 switch 없이** 확장한다.

- `OrderInfoService`, `UserInfoService`, `AuthService`, `ShortUrlService` 등을 주입해 사용  
- 예: `APPROVAL_*` → 주문·사용자·문서번호·승인일 (`AbstractApprovalDataResolver` 공유), `SHORT_URL` → 단축 URL, `AUTH_CODE` → 인증번호, `SETTLEMENT_RESULT` → 정산 결과 등  

`ExternalMessageDataService` / `DefaultExternalMessageDataService` 단일 진입점은 제거되었다.

---

## 전문 / 전송 원칙

- 전문: `[EAI Header] + [채널별 Body]` 문자열  
- JSON 직렬화 전송 없음  
- `Content-Type: text/plain; charset=UTF-8`  

```java
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

---

## 헤더 재사용

`String.format` 등 고정길이 패딩은 `EaiHeaderGenerator`에 두고, `EaiHeaderFactory`가 `MessageSendService`에서 호출되는 진입점이다. Body UTF-8 바이트 길이는 `MessageSendService`에서 계산해 헤더 생성에 넘긴다.

---

## HTTP 통신 분리

`EaiHttpClient`만 실제 HTTP를 호출한다. Resolver / ContentBuilder / Renderer는 HTTP를 호출하지 않는다.

---

## 예외 처리 기준

- `TalkService` / `MessageSendService`: 필수값 null 등 → `IllegalArgumentException`  
- Registry `require(...)`: 해당 enum에 구현 없음 → `IllegalArgumentException`  
- Registry 생성자: 동일 키 중복 등록 → `IllegalStateException`  
- `EaiHttpClient`: HTTP 4xx/5xx 또는 IO 오류 → `IllegalStateException`  

---

## 패키지 구조

```text
com.example.eaimessage
├─ model
├─ header
├─ builder
│  └─ content
├─ renderer
├─ resolver
├─ registry
├─ service
└─ client
```

---

## 구현 원칙 요약

1. 문자열 전문(Header + Body), JSON 전송 금지  
2. 헤더 생성 로직 재사용  
3. DTO는 최소 공통 필드, 나머지는 `params`  
4. 데이터(Resolver) / 문구(ContentBuilder) / 포맷(Renderer) 삼분할 + Registry 선택  
5. 중앙 `switch (messageType)` 없이 확장  
6. HTTP는 클라이언트 한곳  
7. 도메인 서비스 빈은 stateless 싱글톤 운영을 권장  

---

## 새 메시지 타입 추가 체크리스트

1. `MessageType` enum 상수 추가  
2. `MessageDataResolver` 구현 `@Component` 1개  
3. `MessageContentBuilder` 구현 `@Component` 1개  
4. 필요 시 DTO·`params` 규약 정리  

`MessageSendService`, 세 Registry 클래스, 기존 다른 타입의 Resolver/Renderer 소스는 수정하지 않아도 된다 (Spring이 새 빈을 리스트에 포함).

---

## 사용 예시 (호출부)

```java
public void sendUms(Map<String, Object> data) {
    TalkRequest request = TalkRequest.builder()
        .channelType(ChannelType.KTALK)
        .messageType(MessageType.SHORT_URL)
        .receiverId("user-1001")
        .params(data)
        .build();

    talkService.send(request);
}
```

---

## 샘플 실행

`SampleTalkRunner`가 샘플 요청으로 `TalkService`를 호출한다.

기본 URL: `http://localhost:8081/eai/send`

```properties
eai.endpoint=http://your-eai-host/eai/send
sample.run=true
```
