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
  - `A_TALK` (`channelInterfaceId`: `ATK0001`)
  - `EMAIL` (`channelInterfaceId`: `EML0001`)
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
  - `DefaultEaiHeaderGenerator`
    - Header의 IF_ID는 `ChannelType` enum의 `channelInterfaceId`를 사용
    - 채널별 클래스는 시스템 코드/거래 ID 생성 책임만 유지
  - `DefaultEaiBodyGenerator`
    - Header와 동일한 고정 길이 상수/필드명 패턴 적용
    - `MessageContentDto`의 `title`, `content`를 Body에 고정 길이로 적재

## 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.A_TALK)
    .messageType(MessageType.A_MESSAGE)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```
