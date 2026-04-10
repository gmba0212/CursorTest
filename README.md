# EAI Message Common Module

Spring Boot 기반 메시지 발송 공통 모듈입니다.  
메일(EMAIL), 문자(SMS), 알림톡(KTALK)을 공통 구조로 처리합니다.

## 목표

업무 코드에서는 아래 한 줄로 발송을 요청한다.

```java
talkService.send(request);
```

모듈이 담당하는 일:

1. 전문 생성 (Header + Body 문자열)  
2. 중간 데이터 조회 (메시지 타입별 `MessageDataClient`)  
3. HTTP 전송 및 응답 코드 검증  

---

## 핵심 구조

- 진입: `TalkService` → `MessageSendService`로 위임  
- 요청 DTO: `TalkRequest` — `channelType`, `messageType`, `title`, 수신자 필드, `content` 등 **명시 필드만**  
- **데이터 조회**: `MessageSendService`가 `List<MessageDataClient>`를 주입받아 `messageType`별 `EnumMap`으로 선택 → `fetch(request)` → `MessageContext`  
- **문구·템플릿 스냅샷**: `MessageContentComposer`가 `MessageDataClient` 결과와 `TalkRequest`를 합쳐 `MessageContext`에 `templateCode`, `subject`, `content` 등을 채움 (채널별 기본값 보정 포함)  
- **Body 조립**: `BodyBuilderFactory`가 `messageType`별 `BodyBuilder`를 선택 → Body 문자열  
- **Header**: `HeaderBuilderFactory` → `HeaderBuilder` 구현체가 헤더 전문을 만든다 ([`DefaultHeaderBuilder`](https://github.com/gmba0212/CursorTest/blob/main/src/main/java/com/example/eaimessage/builder/header/DefaultHeaderBuilder.java))  
- 전송: `EaiHttpClient`  

타입별 데이터 클라이언트·Body 빌더는 `@Component`로 등록되고, **팩터리가 `EnumMap`으로 구현을 고른다**. `MessageSendService` 본문에는 `switch (messageType)`으로 분기하지 않는다.

### 전문 조립 (헤더·바디)

헤더는 [`DefaultHeaderBuilder`](https://github.com/gmba0212/CursorTest/blob/main/src/main/java/com/example/eaimessage/builder/header/DefaultHeaderBuilder.java) 한 클래스가 담당한다. 바디는 **메시지 타입(`MessageType`)마다 전용 클래스**가 있으며, 같은 맥락에서 “이 전문 종류의 바디를 만든다”는 점이 헤더 빌더와 대응된다.

| MessageType | Body 작성 클래스 (`builder/body`) |
|-------------|----------------------------------|
| `APPROVAL_REQUEST` | `ApprovalRequestBodyBuilder` |
| `APPROVAL_COMPLETE` | `ApprovalCompleteBodyBuilder` |
| `SHORT_URL` | `ShortUrlBodyBuilder` |
| `AUTH_CODE` | `AuthCodeBodyBuilder` |
| `SIMPLE_NOTICE` | `SimpleNoticeBodyBuilder` |
| `SETTLEMENT_RESULT` | `SettlementResultBodyBuilder` |

공통 인터페이스는 `BodyBuilder`이고, `BodyBuilderFactory`가 `supportedType()`으로 구현을 고른다.

### `content` 필드 관례 (단순화)

동일한 `TalkRequest` 스키마로 타입마다 의미만 다르게 쓴다.

| messageType | `content` 용도 (예) |
|-------------|----------------------|
| `SHORT_URL` | 단축할 **원본 URL** |
| `APPROVAL_REQUEST`, `APPROVAL_COMPLETE` | **주문/문서 번호** (문서번호·조회 키) |
| `SETTLEMENT_RESULT` | **주문 번호** (정산 조회 키) |
| `SIMPLE_NOTICE`, `AUTH_CODE` | 안내 본문 / (인증은 데이터 클라이언트가 코드 발급) |

호출부는 위 관례에 맞게 `content`를 채우고, `MessageDataClient`는 필요 시 `OrderInfoService` 등으로 보강한다.

---

## 전체 호출 순서

`talkService.send(request)` 한 번의 내부 진행 순서이다.

### 단계 요약 (번호 순)

1. 호출부에서 `TalkRequest` 생성 (`channelType`, `messageType`, 수신자, `title`/`content` 등).  
2. `TalkService.send(request)` → `MessageSendService.send(request)`.  
3. `MessageSendService`에서 `channelType` / `messageType` null 검증.  
4. `MessageDataClient.fetch(request)` → **`MessageContext`** (타입별 조회 결과).  
5. `MessageContentComposer.compose(request, rawContext)` → **`MessageContext`** (템플릿·제목·본문 등 반영).  
6. `BodyBuilderFactory.get(messageType).build(request, context)` → **Body 문자열**.  
7. `HeaderBuilderFactory.get().build(...)` (body UTF-8 길이 포함) → **Header 문자열**.  
8. `payload = header + body`.  
9. `EaiHttpClient.send(HttpSendRequest)` — `text/plain; charset=UTF-8` POST.  

### 순서도 (상세)

```text
호출부 (TalkRequest 생성)
  -> TalkService.send(request)
       -> MessageSendService.send(request)
            [1] request 검증 (channelType, messageType null 불가)

            [2] 데이터 조회 — messageType 기준 (EnumMap)
            -> MessageDataClient.fetch(request) -> MessageContext

            [3] 문구·컨텍스트 보강
            -> MessageContentComposer.compose(request, rawContext) -> MessageContext

            [4] Body 조립 — messageType 기준
            -> BodyBuilderFactory.get(messageType)
            -> BodyBuilder.build(request, context) -> body

            [5] 헤더 + 전송
            -> HeaderBuilderFactory.get().build(...)
            -> payload = header + body
            -> EaiHttpClient.send
```

### 선택 표

| 단계 | 역할 | 선택 방식 | 선택 키 | 구현 예 |
|------|------|-----------|---------|---------|
| 데이터 | 외부 연동·조회 | `MessageSendService` 내 `EnumMap` | `MessageType` | `ApprovalRequestDataClient`, `ShortUrlDataClient`, … |
| 문구 | 템플릿 코드·제목·본문 | `MessageContentComposer` | `MessageType` (내부 `switch`) | 타입별 분기 |
| Body | 전문 Body 문자열 | `BodyBuilderFactory` | `MessageType` | `ApprovalRequestBodyBuilder`, … |

### 팩터리 공통 패턴 (의사코드)

```java
for (Each impl : injectedList) {
    if (map.put(impl.supportedType(), impl) != null) throw duplicate;
}
```

---

## 인터페이스 요약

**MessageDataClient**

```java
MessageType supportedType();
MessageContext fetch(TalkRequest request);
```

**MessageContentComposer**

```java
MessageContext compose(TalkRequest request, MessageContext rawContext);
```

**BodyBuilder**

```java
MessageType supportedType();
String build(TalkRequest request, MessageContext context);
```

**HeaderBuilder**

```java
String build(String transactionId, ChannelType channelType, MessageType messageType, int bodyLength);
```

Body 빌더는 `MessageContext`에 담긴 `templateCode`, `subject`, `content` 등과 `TalkRequest`의 채널·수신자 정보로 Body 문자열을 만든다.

---

## DTO / 모델

**TalkRequest**

- `channelType`, `messageType`  
- `title`, `receiverType`, `receiverAddress`, `receiverId`, `content`  

**MessageContext**

- 데이터 클라이언트 조회 결과와 컴포저가 채운 키-값 (`getString`, `data()`).  

**Enum**

- `ChannelType`: `KTALK`, `EMAIL`, `SMS`  
- `MessageType`: `APPROVAL_REQUEST`, `APPROVAL_COMPLETE`, `SHORT_URL`, `AUTH_CODE`, `SIMPLE_NOTICE`, `SETTLEMENT_RESULT`  

---

## 외부 서비스 연동

`client.data` 패키지의 `MessageDataClient` 구현에서 도메인 서비스를 호출한다.

- `OrderInfoService`, `UserInfoService`, `AuthService`, `ShortUrlService` 등  
- `APPROVAL_*`: `content`를 주문/문서 번호로 사용, `UserInfoService` 등으로 표시명 보강  
- `SHORT_URL`: `content`를 장 URL로 `ShortUrlService`에 전달  
- `SETTLEMENT_RESULT`: `content`를 주문 번호로 정산 조회  

---

## 전문 / 전송 원칙

- 전문: `[EAI Header] + [Body]`  
- JSON 전송 없음, `Content-Type: text/plain; charset=UTF-8`  

---

## HTTP 통신 분리

`EaiHttpClient`만 실제 HTTP 호출. 데이터 클라이언트·컴포저·Body 빌더는 HTTP를 사용하지 않는다.

---

## 예외 처리 기준

- `TalkService` / `MessageSendService`: 필수값 null → `IllegalArgumentException`  
- `MessageSendService`: 해당 타입의 `MessageDataClient` 없음 → `IllegalArgumentException`  
- `BodyBuilderFactory` / `MessageSendService` 생성자: 중복 키 → `IllegalStateException`  
- `MessageContentComposer`: 미지원 `messageType` → `IllegalArgumentException`  
- `EaiHttpClient`: HTTP 오류 / IO → `IllegalStateException`  

---

## 패키지 구조

```text
com.example.eaimessage
├─ EaiMessageModuleApplication.java
├─ SampleTalkRunner.java
├─ model
│  ├─ ChannelType.java
│  ├─ MessageType.java
│  ├─ TalkRequest.java
│  ├─ MessageContext.java
│  └─ HttpSendRequest.java
├─ service
│  ├─ TalkService.java
│  ├─ MessageSendService.java
│  ├─ MessageContentComposer.java
│  ├─ OrderInfoService.java / DefaultOrderInfoService.java
│  ├─ UserInfoService.java
│  ├─ AuthService.java
│  ├─ ShortUrlService.java / DefaultShortUrlService.java
│  └─ …
├─ client
│  ├─ EaiHttpClient.java
│  └─ data
│     ├─ MessageDataClient.java
│     ├─ AbstractApprovalDataClient.java
│     ├─ ApprovalRequestDataClient.java
│     ├─ ApprovalCompleteDataClient.java
│     ├─ ShortUrlDataClient.java
│     ├─ AuthCodeDataClient.java
│     ├─ SimpleNoticeDataClient.java
│     └─ SettlementResultDataClient.java
├─ builder
│  ├─ factory
│  │  ├─ BodyBuilderFactory.java
│  │  └─ HeaderBuilderFactory.java
│  ├─ header
│  │  ├─ HeaderBuilder.java
│  │  └─ DefaultHeaderBuilder.java
│  └─ body
│     ├─ BodyBuilder.java
│     ├─ AbstractBodyBuilderSupport.java
│     ├─ ApprovalRequestBodyBuilder.java
│     ├─ ApprovalCompleteBodyBuilder.java
│     ├─ ShortUrlBodyBuilder.java
│     ├─ AuthCodeBodyBuilder.java
│     ├─ SimpleNoticeBodyBuilder.java
│     └─ SettlementResultBodyBuilder.java
```

---

## 구현 원칙 요약

1. 문자열 전문(Header + Body), JSON 전송 금지  
2. 헤더는 `HeaderBuilder` 구현(`DefaultHeaderBuilder`), 바디는 `MessageType`별 `BodyBuilder`로 나눈다  
3. 요청은 `TalkRequest` 명시 필드만 사용  
4. 데이터(`MessageDataClient`) / 문구 보강(`MessageContentComposer`) / Body(`BodyBuilder`) 역할 분리  
5. 타입별 확장은 `@Component` + 팩터리·`EnumMap` 조합으로 중앙 분기 최소화  
6. HTTP는 `EaiHttpClient` 한곳  

---

## 새 메시지 타입 추가 체크리스트

1. `MessageType` enum 상수 추가  
2. `MessageDataClient` `@Component` 1개 (`fetch`로 `MessageContext` 구성)  
3. `BodyBuilder` `@Component` 1개  
4. `MessageContentComposer`에 해당 타입 분기 추가  
5. 타입별 `content`/수신자 필드 **관례를 README에 한 줄 추가** (필요 시)  

`MessageSendService`의 `EnumMap` 등록은 새 `MessageDataClient` 빈이 생기면 자동으로 포함된다. `BodyBuilderFactory`도 동일하다.

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
