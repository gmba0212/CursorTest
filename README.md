# EAI Message Common Module

Spring Boot 기반 메시지 발송 공통 모듈입니다.
메일(EMAIL), 문자(SMS), 알림톡(KTALK)을 공통 구조로 처리합니다.

## 핵심 구조

- 진입 서비스: `TalkService`
- 요청 DTO: `TalkRequest` (최소 공통 필드 + `params`)
- 선택 구조: `MessageBuilderFactory` (`channelType + messageType`)
- 생성 구조: 채널별 Builder (`KTalkMessageBuilder`, `EmailMessageBuilder`, `SmsMessageBuilder`)
- 전송 구조: `EaiHttpClient`

## 처리 흐름

1. 호출부에서 `TalkRequest` 생성
2. `talkService.send(request)` 호출
3. `MessageBuilderFactory`가 `(channelType, messageType)`로 Builder 선택
4. Builder가
   - `ExternalMessageDataService`로 중간 데이터 조회
   - `EaiHeaderFactory`로 Header 생성
   - 채널별 Body 생성
   - `header + body` 결합
5. `EaiHttpClient`가 외부 시스템으로 HTTP 전송

## 전문/전송 원칙

- 전문은 문자열 기반
  - `[EAI Header] + [채널별 Body]`
- JSON 직렬화 전송 미사용
- `Content-Type: text/plain; charset=UTF-8`
- 전송 코드:

```java
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

## 헤더 재사용

기존 `String.format("%-Ns", value)` 기반 고정길이 패딩 방식을
`EaiHeaderGenerator`에서 재사용하고, Builder에서는 `EaiHeaderFactory`만 호출합니다.

## 샘플 실행

`SampleTalkRunner`가 샘플 요청을 생성해 `TalkService`를 호출합니다.

기본 전송 URL:

`http://localhost:8081/eai/send`

환경설정:

```properties
eai.endpoint=http://your-eai-host/eai/send
sample.run=true
```
