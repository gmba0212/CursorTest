# EAI Message Common Module

메시지 타입별 콘텐츠 조회와 공통 제너레이터 조합에 집중한 단순화 구조입니다.

## 핵심 구조

- `TalkService` → `MessageSendService`
- `MessageSendService` → `MessageGeneratorFactory.generate(request)`
- `MessageGeneratorFactory`
  1. `MessageType` 기반 `MessageContentProvider` 선택
  2. provider 내부 서비스(`AMessageContentService`, `BMessageContentService`) 호출로 `title/content` 조회
  3. `EaiBodyGenerator`로 Body 문자열 생성
  4. `EaiHeaderGenerator`로 Header 문자열 생성 (`TITLE`, `CONTENT` 포함)
  5. `header + body` 반환
- `EaiHttpClient` 전송

## 모델

- `MessageType`
  - `A_MESSAGE`
  - `B_MESSAGE`
- `TalkRequest`
  - `messageType`
  - `receiverId`
- `MessageContentDto`
  - `title`
  - `content`

## 패키지

- `generator.header`
  - `EaiHeaderGenerator`
  - `DefaultEaiHeaderGenerator`
- `generator.body`
  - `EaiBodyGenerator`
  - `DefaultEaiBodyGenerator`
- `content`
  - `MessageContentProvider`
  - `AMessageContentProvider`
  - `BMessageContentProvider`
  - `MessageContentDto`
- `factory`
  - `MessageGeneratorFactory`
- `service`
  - `MessageSendService`

## 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .messageType(MessageType.A_MESSAGE)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```
