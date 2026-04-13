# EAI Message Common Module

`ChannelType(헤더)`와 `MessageType(바디)` 책임을 분리한 조합형 메시지 생성 모듈입니다.

## 아키텍처

- `TalkService` → `MessageSendService`
- `MessageSendService` → `MessageGeneratorFactory.generate(request)`
- `MessageGeneratorFactory`
  1. `ChannelType` 기반 `EaiHeaderGenerator` 선택
  2. `MessageType` 기반 `EaiBodyGenerator` 선택
  3. `BodyGenerator`가 메시지 타입 전용 서비스 호출 + 공통 바디 포맷에 가변값 채움
  4. Header 생성 후 `header + body` 반환
- `EaiHttpClient` 전송

## 모델

- `ChannelType`
  - `A_TALK` (`channelInterfaceId`: `ATK0001`)
  - `EMAIL` (`channelInterfaceId`: `EML0001`)
- `MessageType`
  - `A_DOCUMENT`
  - `B_DOCUMENT`
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
  - `EaiBodyGenerator` (`supportMessageType`, `generate`)
  - `DefaultEaiBodyGenerator` (공통 포맷 베이스)
  - `ADocumentBodyGenerator`
  - `BDocumentBodyGenerator`
  - `BodyGenerationResult`
- `factory`
  - `MessageGeneratorFactory`

## 확장 방법

새 메시지 타입 추가 시:
1. `MessageType` enum에 타입 추가
2. 새 `EaiBodyGenerator` 구현체 추가
3. 필요 서비스 주입
4. 스프링 컴포넌트 스캔으로 자동 매핑

공통 전송 서비스(`MessageSendService`)와 팩토리 분기 로직 수정 없이 확장됩니다.

## 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.EMAIL)
    .messageType(MessageType.A_DOCUMENT)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```
