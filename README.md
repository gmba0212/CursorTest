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
- 요청 DTO: `TalkRequest` — **명시 필드만** (`channelType`, `messageType`, `title`, 수신자 필드, `content`). **범용 `params` 맵 없음.**  
- **데이터 조회 선택**: `MessageDataResolverRegistry` — `messageType` → `MessageDataResolver` → `ServiceData`  
- **문구 생성 선택**: `MessageContentBuilderRegistry` — `messageType` → `MessageContentBuilder` → `MessageContent`  
- **채널 포맷 선택**: `ChannelRendererRegistry` — `channelType` → `ChannelMessageRenderer` → Body 문자열  
- 헤더: `EaiHeaderFactory`  
- 전송: `EaiHttpClient`  
- 알림톡 senderKey 등: `eaimessage.ktalk.*` 설정 (`KTalkProperties`)  

타입별 데이터(resolver), 타입별 문구(content builder), 채널별 렌더러(renderer)는 `@Component`로 등록되고 **레지스트리가 `EnumMap`으로 구현만 고른다**. 중앙 서비스에 `switch (messageType)`으로 나열하지 않는다.

### `params`를 쓰지 않는 이유 (설계)

- 임의 key-value는 **어떤 키가 유효한지 컴파일 타임에 알 수 없고**, 오타·누락이 런타임까지 숨는다.  
- Resolver / Builder / Renderer가 **암묵적으로 같은 문자열 키**에 의존하게 된다.  
- `channelType`·`messageType`으로 이미 라우팅되므로, 요청 본문은 **명시 필드 + Resolver가 채운 `ServiceData`** 로 단순화하는 편이 안전하다.  

추가 업무 값이 필요하면 **외부 서비스 조회 결과(`ServiceData`)** 또는 **향후 `TalkRequest`에 필드 추가**로 확장한다 (맵으로 우회하지 않음).

### `content` 필드 관례 (단순화)

동일한 `TalkRequest` 스키마로 타입마다 의미만 다르게 쓴다.

| messageType | `content` 용도 (예) |
|-------------|----------------------|
| `SHORT_URL` | 단축할 **원본 URL** |
| `APPROVAL_REQUEST`, `APPROVAL_COMPLETE` | **주문/문서 번호** (문서번호·조회 키) |
| `SETTLEMENT_RESULT` | **주문 번호** (정산 조회 키) |
| `SIMPLE_NOTICE`, `AUTH_CODE` | 안내 본문 / (인증은 Resolver가 코드 발급) |

호출부는 위 관례에 맞게 `content`를 채우고, Resolver는 필요 시 `OrderInfoService` 등으로 보강한다.

---

## 전체 호출 순서

`talkService.send(request)` 한 번의 내부 진행 순서이다.

### 단계 요약 (번호 순)

1. 호출부에서 `TalkRequest` 생성 (`channelType`, `messageType`, 수신자, `title`/`content` 등).  
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
            -> MessageDataResolver.resolve(request) -> ServiceData

            [3] 문구 생성 — messageType 기준
            -> MessageContentBuilderRegistry.require(messageType)
            -> MessageContentBuilder.build(request, serviceData) -> MessageContent

            [4] 채널 포맷 — channelType 기준
            -> ChannelRendererRegistry.require(channelType)
            -> ChannelMessageRenderer.renderBody(...) -> body

            [5] 헤더 + 전송
            -> EaiHeaderFactory.createHeader(...)
            -> payload = header + body
            -> EaiHttpClient.send
```

### 레지스트리 선택 표

| 단계 | 역할 | 레지스트리 | 선택 키 | 구현 예 |
|------|------|------------|---------|---------|
| 데이터 | 외부 데이터 수집 | `MessageDataResolverRegistry` | `MessageType` | `ApprovalRequestDataResolver`, `ShortUrlDataResolver`, … |
| 문구 | 제목·본문·템플릿 코드 | `MessageContentBuilderRegistry` | `MessageType` | `ApprovalRequestContentBuilder`, … |
| 포맷 | 고정길이 Body 조립 | `ChannelRendererRegistry` | `ChannelType` | `KTalkRenderer`, `SmsRenderer`, `EmailRenderer` |

### Registry 공통 패턴 (의사코드)

```java
for (Each impl : injectedList) {
    if (map.put(impl.key(), impl) != null) throw duplicate;
}
```

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

## DTO / Enum

**TalkRequest**

- `channelType`, `messageType`  
- `title`, `receiverType`, `receiverAddress`, `receiverId`, `content`  
- Resolver → `ServiceData` → ContentBuilder → `MessageContent`  

**Enum**

- `ChannelType`: `KTALK`, `EMAIL`, `SMS`  
- `MessageType`: `APPROVAL_REQUEST`, `APPROVAL_COMPLETE`, `SHORT_URL`, `AUTH_CODE`, `SIMPLE_NOTICE`, `SETTLEMENT_RESULT`  

**MessageContent**  
채널과 무관한 템플릿 코드·제목·본문 스냅샷.

---

## 설정 (알림톡 등)

`src/main/resources/application.properties`:

```properties
eaimessage.ktalk.sender-key=DEFAULT_KTALK_KEY
```

`KTalkRenderer`는 `KTalkProperties.getSenderKey()`만 사용한다 (요청 맵 없음).

---

## 외부 서비스 연동

Resolver에서 도메인 서비스를 호출한다. 타입마다 Resolver 클래스로 **중앙 switch 없이** 확장.

- `OrderInfoService`, `UserInfoService`, `AuthService`, `ShortUrlService` 등  
- `APPROVAL_*`: `content`를 주문/문서 번호로 사용, `UserInfoService.getDisplayName(receiverId)`로 표시명  
- `SHORT_URL`: `content`를 장 URL로 `ShortUrlService`에 전달  
- `SETTLEMENT_RESULT`: `content`를 주문 번호로 정산 조회  

---

## 전문 / 전송 원칙

- 전문: `[EAI Header] + [채널별 Body]`  
- JSON 전송 없음, `Content-Type: text/plain; charset=UTF-8`  

---

## HTTP 통신 분리

`EaiHttpClient`만 실제 HTTP 호출. Resolver / ContentBuilder / Renderer는 HTTP 미사용.

---

## 예외 처리 기준

- `TalkService` / `MessageSendService`: 필수값 null → `IllegalArgumentException`  
- Registry `require(...)`: 미등록 타입/채널 → `IllegalArgumentException`  
- Registry 생성자: 중복 키 → `IllegalStateException`  
- `EaiHttpClient`: HTTP 오류 / IO → `IllegalStateException`  

---

## 패키지 구조

```text
com.example.eaimessage
├─ config         (KTalkProperties 등)
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
3. 요청은 `TalkRequest` 명시 필드만, **params 맵 없음**  
4. 데이터(Resolver) / 문구(ContentBuilder) / 포맷(Renderer) 삼분할 + Registry  
5. 중앙 `switch (messageType)` 없이 확장  
6. HTTP는 클라이언트 한곳  

---

## 새 메시지 타입 추가 체크리스트

1. `MessageType` enum 상수 추가  
2. `MessageDataResolver` `@Component` 1개 (`TalkRequest` 필드 + 외부 서비스로 `ServiceData` 구성)  
3. `MessageContentBuilder` `@Component` 1개  
4. 타입별 `content`/수신자 필드 **관례를 README에 한 줄 추가** (필요 시)  

`MessageSendService`, 세 Registry 클래스, 기존 다른 타입 구현은 수정하지 않아도 된다.

---

## 사용 예시 (호출부)

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.KTALK)
    .messageType(MessageType.SHORT_URL)
    .receiverId("user-1001")
    .receiverAddress("01012341234")
    .title("[단축URL] 안내")
    .content("https://example.com/orders/ORD-001")
    .build();

talkService.send(request);
```

---

## 샘플 실행

`SampleTalkRunner`가 샘플 요청으로 `TalkService`를 호출한다.

```properties
eai.endpoint=http://your-eai-host/eai/send
sample.run=true
```
