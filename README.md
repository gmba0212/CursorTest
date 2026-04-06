# EAI Message Common Module

Spring Boot 기반으로 알림톡/메일/SMS를 공통 전송 구조로 처리하는
메시지 모듈 예제입니다.

## 핵심 설계

- Factory + Builder 패턴 적용
- 채널별 Builder 1개 구조
  - `ATalkMessageBuilder`
  - `MailMessageBuilder`
  - `SmsMessageBuilder`
- 채널별 Builder 내부에서 `messageType` 분기 처리
- 최종 전송 전문은 JSON이 아닌 문자열
  - `header 문자열 + body 문자열`

## 패키지 구조

- `model`
- `builder`
- `factory`
- `client`
- `service`

## 전송 흐름

1. `MessageSendService`가 요청 수신
2. `MessageBuilderFactory`가 채널별 Builder 선택
3. Builder가 `ATalkHeaderSendData` / `ATalkBodySendData`로 문자열 전문 생성
4. `EaiHttpClient`가 `text/plain; charset=UTF-8`로 HTTP POST 전송

## 주요 요구사항 반영

- `httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));`
- `Content-Type: text/plain; charset=UTF-8`
- DTO/Map/JSON 직렬화 전송 없음

## 샘플 실행

`SampleMessageRunner`는 애플리케이션 시작 시 샘플 요청을 생성해
`MessageSendService`를 호출합니다.

기본 전송 URL:

`http://localhost:8081/eai/send`

환경설정으로 변경 가능:

```properties
eai.endpoint=http://your-eai-host/eai/send
```
