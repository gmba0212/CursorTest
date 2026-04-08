# 메시지 발송 공통 모듈 설계/구현 가이드 (README2)

## 1. 목표

여러 업무 코드에서 아래 한 줄 수준으로 재사용 가능한 메시지 공통 모듈을 제공한다.

```java
talkService.send(request);
```

모듈 내부에서 다음을 모두 처리한다.

1) 전문 생성 (Header + Body, 고정길이 문자열)  
2) 중간 데이터 조회 (서비스 호출)  
3) HTTP 전송 및 결과코드 검증

---

## 2. 최종 처리 흐름

```text
호출부
  -> TalkService.send(request)
      -> MessageBuilderFactory.getBuilder(messageType, channelType)
      -> 선택된 Builder.build(request)
          -> ExternalMessageDataService.resolve(request)   // 중간 서비스 호출
          -> Header 생성 (기존 포맷 재사용)
          -> Body 생성 (채널/타입별)
          -> payload = header + body
      -> EaiHttpClient.send(payload)
      -> 응답코드 확인(성공/실패)
```

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
- Builder에서 `params + 서비스조회결과`를 조합한다.

### 4.2 Enum

- `ChannelType`: `KTALK`, `EMAIL`, `SMS`
- `MessageType`:  
  `APPROVAL_REQUEST`, `APPROVAL_COMPLETE`, `SHORT_URL`,  
  `AUTH_CODE`, `SIMPLE_NOTICE`, `SETTLEMENT_RESULT`

---

## 5. Header 재사용 구조 (핵심)

헤더는 기존 고정길이/패딩 생성 방식(`String.format`)을 그대로 활용한다.

### 5.1 기존 패턴 유지

```java
String.format("%-" + length + "s", value)
```

예시:

```java
private static final String TEST_TP_DSCD = String.format("%-1s", "0");
private static final String SYS_CD = String.format("%-10s", "SYSTEM");
private static final String IF_ID = String.format("%-10s", "IF0001");
```

### 5.2 역할 분리

- `EaiHeaderGenerator`: 기존 포맷 로직 보유
- `EaiHeaderFactory`: Builder에서 호출하는 진입점
- Builder는 `headerFactory.createHeader(...)`만 사용

---

## 6. Builder + Factory

### 6.1 MessageBuilder 인터페이스

```java
boolean supports(ChannelType channelType, MessageType messageType);
MessagePayload build(TalkRequest request);
```

### 6.2 MessageBuilderFactory

```java
MessageBuilder getBuilder(MessageType messageType, ChannelType channelType)
```

등록된 Builder 중 `supports(channelType, messageType)`가 true인 Builder를 선택한다.

### 6.3 Builder 구현

- `KTalkMessageBuilder`
- `EmailMessageBuilder`
- `SmsMessageBuilder`

각 Builder는 다음을 수행한다.

1) request 검증  
2) 서비스 데이터 조회 결과 반영  
3) Header 생성 (재사용 로직 호출)  
4) Body 생성  
5) payload 결합

---

## 7. 외부 서비스 연동 구조

Builder 내부에서 DB/외부 직접 접근하지 않고, 반드시 서비스를 통해 조회한다.

- `ExternalMessageDataService` (조합 진입점)
- `OrderInfoService`
- `UserInfoService`
- `AuthService`
- `ShortUrlService`

`DefaultExternalMessageDataService`에서 MessageType 기준으로 조회/조합:

- `APPROVAL_*` -> 주문/사용자 정보
- `SHORT_URL` -> shortUrlService 호출
- `AUTH_CODE` -> 인증번호 조회
- `SETTLEMENT_RESULT` -> 정산 결과 조회

---

## 8. HTTP 통신 분리

`EaiHttpClient`에서만 외부 HTTP 호출을 수행한다.

```java
httpPost.setHeader("Content-Type", "text/plain; charset=UTF-8");
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

- Builder는 HTTP 호출하지 않는다.
- 공통 로깅/예외/응답처리 확장 지점을 클라이언트에 집중한다.

---

## 9. 응답/예외 처리 기준

- `TalkService`: 입력 필수값 검증 실패 시 `IllegalArgumentException`
- `MessageBuilderFactory`: 미지원 조합 시 `IllegalArgumentException`
- `EaiHttpClient`:
  - HTTP 400 이상 -> `IllegalStateException`
  - I/O 예외 -> `IllegalStateException`

---

## 10. 패키지 구조

```text
com.example.eaimessage
├─ model
├─ header
├─ builder
├─ factory
├─ service
└─ client
```

복잡한 추가 계층 없이 최소 책임 분리만 유지한다.

---

## 11. 구현 원칙 요약

1) 문자열 전문 기반(Header + Body), JSON 전송 금지  
2) 기존 헤더 생성 로직 재사용 가능 구조  
3) DTO는 최소 공통 필드만 유지  
4) 채널/메시지타입 기반 Builder + Factory  
5) 중간 데이터 조회는 서비스 호출로 분리  
6) HTTP 통신은 별도 클라이언트 분리  
7) 공통 서비스는 상태 없는(stateless) 싱글톤으로 운영

