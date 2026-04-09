# 메시지 발송 공통 모듈 설계/구현 가이드 (README2)

## 1. 목표

여러 업무 코드에서 아래 한 줄 수준으로 재사용 가능한 메시지 공통 모듈을 제공한다.

```java
talkService.send(request);
```

모듈 내부에서 다음을 모두 처리한다.

1) 전문 생성 (Header + Body, 고정길이 문자열)  
2) 중간 데이터 조회 (타입별 Resolver)  
3) HTTP 전송 및 결과코드 검증

---

## 2. 최종 처리 흐름 (전체 호출 순서)

### 2.1 순서도

```text
호출부 (TalkRequest 생성)
  -> TalkService.send(request)
       -> MessageSendService.send(request)
            [1] request 검증 (channelType, messageType null 불가)

            [2] 데이터 조회 단계 — messageType 기준
            -> MessageDataResolverRegistry.require(messageType)
                 * 생성자에서 Spring이 주입한 List<MessageDataResolver>를
                   순회하며 supportedType() -> EnumMap<MessageType, Resolver> 구성
                 * require는 맵 조회만 수행 (중앙 switch 없음)
            -> 선택된 MessageDataResolver.resolve(request)
                 -> ServiceData

            [3] 문구 생성 단계 — messageType 기준
            -> MessageContentBuilderRegistry.require(messageType)
                 * List<MessageContentBuilder> -> EnumMap (동일 패턴)
            -> 선택된 MessageContentBuilder.build(request, serviceData)
                 -> MessageContent (채널 무관 중간 모델)

            [4] 채널 포맷 단계 — channelType 기준
            -> ChannelRendererRegistry.require(channelType)
                 * List<ChannelMessageRenderer> -> EnumMap
            -> 선택된 ChannelMessageRenderer.renderBody(request, serviceData, content)
                 -> body 문자열 (ATalk / SMS / Email 고정길이 전문)

            [5] 헤더 + 전송
            -> EaiHeaderFactory.createHeader(txId, channelType, messageType, bodyUtf8Length)
            -> payload = header + body
            -> EaiHttpClient.send(HttpSendRequest)
            -> 응답코드 확인(성공/실패)
```

### 2.2 “팩토리에서 빌드를 고르는” 구체적 방식

과거에는 `MessageBuilderFactory`가 `supports(channelType, messageType)`로 **하나의 거대 Builder**를 골랐다.

현재는 책임을 쪼개어 **레지스트리 3종**이 각각 “한 차원”만 고른다.

| 단계 | 역할 | 레지스트리 | 선택 키 | 구현 예 |
|------|------|------------|---------|---------|
| 데이터 | 외부 데이터 수집 | `MessageDataResolverRegistry` | `MessageType` | `ApprovalRequestDataResolver`, `ShortUrlDataResolver`, … |
| 문구 | 제목/본문/템플릿 코드 | `MessageContentBuilderRegistry` | `MessageType` | `ApprovalRequestContentBuilder`, … |
| 포맷 | 고정길이 바디 조립 | `ChannelRendererRegistry` | `ChannelType` | `KTalkRenderer`, `SmsRenderer`, `EmailRenderer` |

- Spring Boot는 위 구현체들을 `@Component`로 스캔한다.
- 각 **Registry의 생성자**는 `List<해당인터페이스>`를 받아 **기동 시 한 번** `EnumMap`을 만든다.
- **중복 `supportedType()` / `channelType()`** 이 있으면 생성자에서 `IllegalStateException`으로 실패시켜 잘못된 빈 등록을 조기에 막는다.
- 런타임에는 `require(enum)`만 호출하므로, `MessageSendService` 본문에 `switch (messageType)`으로 분기하지 않는다.

---

## 3. 사용 예시 (호출부 단순화)

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

호출부는 전문 포맷/HTTP 상세를 몰라도 된다.

---

## 4. DTO/Enum (최소 구조)

### 4.1 TalkRequest

```java
public class TalkRequest {
    private ChannelType channelType;
    private MessageType messageType;
    private String title;
    private String receiverType;
    private String receiverAddress;
    private String receiverId;
    private String content;
    private Map<String, Object> params;
}
```

- 업무별 필드는 DTO에 추가하지 않는다.
- 추가 데이터는 `params`로 전달한다.
- Resolver가 서비스 조회로 `ServiceData`를 채우고, ContentBuilder가 `request` + `ServiceData`로 `MessageContent`를 만든다.

### 4.2 Enum

- `ChannelType`: `KTALK`, `EMAIL`, `SMS`
- `MessageType`:  
  `APPROVAL_REQUEST`, `APPROVAL_COMPLETE`, `SHORT_URL`,  
  `AUTH_CODE`, `SIMPLE_NOTICE`, `SETTLEMENT_RESULT`

### 4.3 MessageContent (중간 모델)

채널과 무관한 템플릿 코드, 제목, 본문 등을 담는 불변에 가까운 스냅샷. Renderer는 `MessageType`을 몰라도 된다.

---

## 5. Header 재사용 구조 (핵심)

헤더는 기존 고정길이/패딩 생성 방식을 그대로 활용한다.

### 5.1 기존 패턴 유지

```java
String.format("%-" + length + "s", value)
```

### 5.2 역할 분리

- `EaiHeaderGenerator`: 기존 포맷 로직 보유
- `EaiHeaderFactory`: `MessageSendService`에서 호출하는 진입점
- Body UTF-8 바이트 길이는 `MessageSendService`에서 계산 후 헤더 팩토리에 전달

---

## 6. Registry + 책임 분리 (기존 Builder/Factory 대체)

### 6.1 MessageDataResolver

```java
MessageType supportedType();
ServiceData resolve(TalkRequest request);
```

- 타입별로 `@Component` 구현 (`ApprovalRequestDataResolver`, `ShortUrlDataResolver`, …).
- 승인 요청/완료처럼 조회 로직이 같으면 `AbstractApprovalDataResolver` 등으로 공유.

### 6.2 MessageContentBuilder

```java
MessageType supportedType();
MessageContent build(TalkRequest request, ServiceData serviceData);
```

- 문구/템플릿 코드만 담당. 채널별 고정길이는 모른다.

### 6.3 ChannelMessageRenderer

```java
ChannelType channelType();
String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content);
```

- `MessageType` 분기 없이 `MessageContent`와 `TalkRequest`의 수신자 등만으로 Body 전문 생성.

### 6.4 Registry 공통 패턴

```java
// 의사코드: 각 Registry 생성자
for (Each impl : injectedList) {
    if (map.put(impl.key(), impl) != null) throw duplicate;
}
```

`MessageSendService`는 세 Registry의 `require`만 호출한다.

---

## 7. 외부 서비스 연동 구조

Resolver 내부에서 DB/외부 API에 접근하되, **타입마다 Resolver 클래스**로 나누어 중앙 `switch`를 두지 않는다.

- `OrderInfoService`, `UserInfoService`, `AuthService`, `ShortUrlService` 등은 기존과 같이 주입받아 사용
- 예시 매핑:
  - `APPROVAL_REQUEST` / `APPROVAL_COMPLETE` → 주문·사용자 정보, 문서번호, 승인일 등 (`AbstractApprovalDataResolver` 공유)
  - `SHORT_URL` → `ShortUrlService`
  - `AUTH_CODE` → `AuthService`
  - `SIMPLE_NOTICE` → 최소 플래그 등
  - `SETTLEMENT_RESULT` → 정산 결과·주문번호

과거의 `ExternalMessageDataService` / `DefaultExternalMessageDataService` 단일 진입점은 제거되었다.

---

## 8. HTTP 통신 분리

`EaiHttpClient`에서만 외부 HTTP 호출을 수행한다.

```java
httpPost.setHeader("Content-Type", "text/plain; charset=UTF-8");
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

- Resolver / ContentBuilder / Renderer는 HTTP 호출하지 않는다.
- 공통 로깅/예외/응답처리 확장 지점을 클라이언트에 집중한다.

---

## 9. 응답/예외 처리 기준

- `TalkService` / `MessageSendService`: 입력 필수값 검증 실패 시 `IllegalArgumentException`
- 각 Registry `require(...)`: 해당 enum에 구현이 없으면 `IllegalArgumentException`
- Registry 생성자: 동일 키 중복 등록 시 `IllegalStateException`
- `EaiHttpClient`:
  - HTTP 400 이상 → `IllegalStateException`
  - I/O 예외 → `IllegalStateException`

---

## 10. 패키지 구조

```text
com.example.eaimessage
├─ model          (TalkRequest, ServiceData, MessageContent, …)
├─ header
├─ builder
│  └─ content     (MessageContentBuilder 구현체)
├─ renderer       (ChannelMessageRenderer 구현체)
├─ resolver       (MessageDataResolver 구현체)
├─ registry       (MessageDataResolverRegistry, MessageContentBuilderRegistry, ChannelRendererRegistry)
├─ service        (TalkService, MessageSendService, 도메인 서비스 인터페이스)
└─ client
```

---

## 11. 구현 원칙 요약

1) 문자열 전문 기반(Header + Body), JSON 전송 금지  
2) 기존 헤더 생성 로직 재사용 가능 구조  
3) DTO는 최소 공통 필드만 유지  
4) **데이터(Resolver) / 문구(ContentBuilder) / 포맷(Renderer)** 삼분할 + Registry로 선택  
5) 중앙 `switch (messageType)` 없이 OCP에 가깝게 확장  
6) HTTP 통신은 별도 클라이언트 분리  
7) 공통 서비스는 상태 없는(stateless) 싱글톤으로 운영

---

## 12. 새 메시지 타입 추가 체크리스트

1. `MessageType` enum에 상수 추가  
2. `MessageDataResolver` 구현 `@Component` 1개  
3. `MessageContentBuilder` 구현 `@Component` 1개  
4. (필요 시) DTO·`params` 규약 문서화  

기존 `MessageSendService`, 세 Registry 클래스, 기존 Renderer/다른 타입 Resolver 소스는 수정하지 않아도 된다 (Spring이 새 빈을 리스트에 포함).
