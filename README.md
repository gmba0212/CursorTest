# EAI Message Common Module

Spring Boot 기반 메시지 발송 공통 모듈입니다.  
메일(EMAIL), 문자(SMS), 알림톡(KTALK)을 공통 구조로 처리합니다.

## 목표

업무 코드는 아래 최소 요청만 생성합니다.

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.KTALK)
    .messageType(MessageType.SHORT_URL)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```

모듈이 담당하는 일:

1. messageType별 내부 조회/조합으로 컨텐츠 생성
2. Header + Body 고정길이 전문 생성
3. EAI HTTP 전송 및 응답 검증

---

## 현재 구조 (단순화 버전)

- 진입: `TalkService` → `MessageSendService`
- 요청 DTO: `TalkRequest(channelType, messageType, receiverId)`
- 컨텐츠 생성: `MessageContentProviderFactory(channelType, messageType)` → `MessageContentProvider`
- 헤더 생성: `MessageHeaderGeneratorFactory` → `MessageHeaderGenerator`
- 바디 생성: `MessageBodyGeneratorFactory(channelType, messageType)` → `MessageBodyGenerator`
- 전송: `EaiHttpClient`

핵심 원칙:

1. **컨텐츠 결정은 provider가 담당**  
   (내부 서비스 조회, 제목/본문/템플릿 코드 조합)
2. **generator는 문자열 포맷팅만 담당**  
   (고정 길이 직렬화, 헤더/바디 생성)
3. **factory는 구현 선택만 담당**  
   (fallback/기본값 보정/비즈니스 분기 없음)

---

## 처리 흐름

1. `MessageSendService.send(request)` 호출
2. 요청 검증 (`channelType`, `messageType`, `receiverId`)
3. `MessageContentProviderFactory.get(channelType, messageType)`로 provider 선택
4. provider가 내부 서비스 조회 후 `PreparedMessageContent` 생성
5. `MessageBodyGeneratorFactory.get(channelType, messageType)`로 body generator 선택
6. generator가 `BodyGenerationInput`을 고정길이 Body 문자열로 직렬화
7. `MessageHeaderGenerator`가 Header 생성 (body UTF-8 길이 포함)
8. `payload = header + body`
9. `EaiHttpClient.send(...)` 전송

---

## DTO / 모델

- `TalkRequest`
  - `channelType`
  - `messageType`
  - `receiverId`

- `PreparedMessageContent`
  - provider가 완성한 컨텐츠 스냅샷
  - `templateCode`, `receiverType`, `receiverAddress`, `receiverId`, `subject`, `content`

- `BodyGenerationInput`
  - body generator용 직렬화 입력 모델

---

## 새 메시지 타입 추가 방법

1. `MessageType` enum 값 추가
2. `MessageContentProvider` 구현 1개 추가

필요 시 body 포맷이 달라지면:

3. `MessageBodyGenerator` 구현 추가
4. `MessageBodyGeneratorFactory`에 해당 `(channelType, messageType)` 등록 추가

기존 전송 서비스/헤더 생성 로직 수정은 최소화됩니다.

---

## 설정

`src/main/resources/application.properties`:

```properties
eaimessage.ktalk.sender-key=DEFAULT_KTALK_KEY
```

---

## 예외 처리 기준

- `MessageSendService`: 필수값 누락 시 `IllegalArgumentException`
- Factory: 미등록 route/type 요청 시 `IllegalArgumentException`
- Factory 생성자: 중복 등록 시 `IllegalStateException`
- `EaiHttpClient`: HTTP 오류/IO 오류 시 `IllegalStateException`
