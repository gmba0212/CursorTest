# EAI Message Common Module

`MessageType(콘텐츠)`와 `ChannelType(전송 채널)` 책임을 분리한 조합형 메시지 생성 모듈입니다.

## 아키텍처

- `TalkService` → `MessageSendService`
- `MessageSendService` → `MessageGeneratorFactory.generate(request)`
- `MessageGeneratorFactory`
  1. `MessageType` 기반 `MessageContentProvider` 선택
  2. provider 내부 서비스 호출로 `title/content` 조회
  3. `ChannelType` 기반 `EaiBodyGenerator` 선택 후 Body 생성
  4. `ChannelType` 기반 `EaiHeaderGenerator` 선택 후 Header 생성 (`TITLE`, `CONTENT` 포함)
  5. `header + body` 반환
- `EaiHttpClient` 전송

## 모델

- `ChannelType`
  - `A_TALK`
  - `EMAIL`
- `MessageType`
  - `A_MESSAGE`
  - `B_MESSAGE`
- `TalkRequest`
  - `channelType`
  - `messageType`
  - `receiverId`

## 패키지

- `generator.header`
  - `EaiHeaderGenerator`
  - `DefaultEaiHeaderGenerator` (공통 포맷 베이스)
  - `AtalkHeaderGenerator`
  - `EmailHeaderGenerator`
- `generator.body`
  - `EaiBodyGenerator`
  - `DefaultEaiBodyGenerator` (공통 포맷 베이스)
  - `AtalkBodyGenerator`
  - `EmailBodyGenerator`
- `content`
  - `MessageContentProvider`
  - `AMessageContentProvider`
  - `BMessageContentProvider`
  - `MessageContentDto`
- `factory`
  - `MessageGeneratorFactory`

## 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.A_TALK)
    .messageType(MessageType.A_MESSAGE)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```
