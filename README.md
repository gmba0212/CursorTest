# EAI Message Common Module

`ChannelType(헤더/바디 템플릿)`와 `MessageType(바디 데이터)` 책임을 분리한
조합형 메시지 생성 모듈입니다.

## 아키텍처

- `TalkService` → `MessageSendService`
- `MessageSendService`
  1. `HeaderGeneratorFactory`에서 `ChannelType` 기반 `EaiHeaderGenerator` 선택
  2. `BodyGeneratorFactory`에서 `ChannelType + MessageType` 기반
     `EaiBodyGenerator` 선택
  3. `BodyGenerator`가 메시지 타입별 서비스 호출 후 `BodyData` 생성
  4. 채널별 `DefaultBodyTemplate`이 body 문자열 조립
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
  - `EmailADocumentBodyGenerator`
  - `EmailBDocumentBodyGenerator`
  - `BodyData`
  - `DefaultBodyTemplate` (interface)
  - `ATalkDefaultBodyTemplate`
  - `EmailDefaultBodyTemplate`
- `factory`
  - `HeaderGeneratorFactory`
  - `BodyGeneratorFactory`

## 확장 방법

새 채널 추가 시:
1. `ChannelType` enum에 채널 추가
2. 채널 전용 `EaiHeaderGenerator` 구현체 추가
3. 채널 전용 `DefaultBodyTemplate` 구현체 추가
4. 지원할 각 `MessageType`에 대한 `EaiBodyGenerator` 구현체 추가

기존 채널에 메시지 타입 추가 시:
1. `MessageType` enum에 타입 추가
2. 해당 채널의 `EaiBodyGenerator` 구현체만 추가
3. 기존 채널 `DefaultBodyTemplate`은 재사용

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
