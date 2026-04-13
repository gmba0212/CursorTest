# EAI Message Common Module

`ChannelType(헤더)`와 `MessageType(바디)` 책임을 분리한 조합형 메시지 생성 모듈입니다.

## 아키텍처

- `TalkService` → `MessageSendService`
- `MessageSendService`
  1. `HeaderGeneratorFactory`에서 `ChannelType` 기반 `EaiHeaderGenerator` 선택
  2. `BodyGeneratorFactory`에서 `MessageType` 기반 `EaiBodyGenerator` 선택
  3. `BodyGenerator`가 메시지 타입별 서비스 호출 후 `BodyData` 생성
  4. `DefaultBodyTemplate`이 고정 길이/패딩/기본값 규칙으로 body 문자열 조립
  5. `HeaderGenerator`가 채널별 값으로 `HeaderData` 생성
  6. `DefaultHeaderTemplate`이 공통 헤더 포맷으로 header 문자열 조립
  7. `header + body` 결합 후 `EaiHttpClient` 전송

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
  - `AtalkHeaderGenerator`
  - `EmailHeaderGenerator`
  - `HeaderData`
  - `DefaultHeaderTemplate`
- `generator.body`
  - `EaiBodyGenerator`
  - `ADocumentBodyGenerator`
  - `BDocumentBodyGenerator`
  - `BodyData`
  - `DefaultBodyTemplate`
- `factory`
  - `HeaderGeneratorFactory`
  - `BodyGeneratorFactory`

## 확장 방법

새 메시지 타입 추가 시:
1. `MessageType` enum에 타입 추가
2. 새 `EaiBodyGenerator` 구현체 추가(내부에서 필요한 서비스 직접 호출)
3. 스프링 컴포넌트 스캔으로 `BodyGeneratorFactory` 자동 매핑

공통 전송 서비스(`MessageSendService`) 수정 없이 확장됩니다.

## 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.EMAIL)
    .messageType(MessageType.A_DOCUMENT)
    .receiverId("user-1001")
    .build();
talkService.send(request);
```
