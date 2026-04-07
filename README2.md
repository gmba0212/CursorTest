# EAI 메시지 공통 모듈 설계 가이드 (README2)

## 1) 문서 목적

이 문서는 현재 저장소에 구현된 **Spring Boot 기반 EAI 메시지 공통 모듈**의
설계 컨셉과 동작 방식을 상세히 설명한다.

핵심 목표는 다음과 같다.

- 알림톡(ALIMTALK) / 메일(MAIL) / SMS를 공통 흐름으로 처리
- 메시지 유형(`MessageType`)별 값 세팅 로직을 채널 내부에서 분기
- 최종 전송 데이터는 JSON이 아닌 **문자열 전문(payload)** 으로 처리
- `header + body` 문자열을 `text/plain`으로 HTTP POST 전송

---

## 2) 핵심 설계 원칙

### 2.1 Builder + Factory 패턴

이 모듈은 다음 조합으로 동작한다.

- `MessageSendService`
  - 요청 진입점
- `MessageBuilderFactory`
  - 채널 타입에 맞는 Builder 선택
- 채널별 Builder (각 1개)
  - `ATalkMessageBuilder`
  - `MailMessageBuilder`
  - `SmsMessageBuilder`
- `EaiHttpClient`
  - 문자열 payload 전송

즉, 구조는 다음과 같다.

```text
MessageSendService
  -> MessageBuilderFactory
      -> (ATalkMessageBuilder | MailMessageBuilder | SmsMessageBuilder)
          -> messageType 분기 + body/header 문자열 생성
  -> EaiHttpClient (HTTP POST)
```

### 2.2 채널별 Builder는 1개만 유지

메시지 유형별 Builder를 분리하지 않고,
채널별 Builder 내부에서 `switch(messageType)`로 분기한다.

장점:

- 과설계 방지
- 채널 단위로 책임 집중
- 유지보수 시 탐색 범위 축소

### 2.3 AbstractMessageBuilder는 채널 중립 템플릿

`AbstractMessageBuilder`는 특정 채널 구현에 고정되지 않는다.

- 공통 역할
  - 입력 검증
  - 공통 유틸(기본값, 수신자 추출, Map 데이터 추출, 트랜잭션 ID)
  - `header + body` 결합 흐름
- 채널별 구체 구현 책임
  - `buildBodyString`
  - `buildHeaderString`

즉, 추상 클래스는 "흐름", 하위 클래스는 "세부 포맷"을 담당한다.

---

## 3) 전송 포맷 원칙 (매우 중요)

### 3.1 최종 페이로드 구조

최종 payload는 아래 규칙을 따른다.

```text
[공통 헤더 문자열] + [메시지 본문 문자열]
```

### 3.2 JSON 미사용

본 모듈은 EAI 전문 송신 모듈이므로 다음을 사용하지 않는다.

- DTO -> JSON 직렬화
- Map 직접 전송
- multipart/form-data

### 3.3 HTTP 전송 방식

`EaiHttpClient`는 Apache HttpClient를 사용하고
본문은 `StringEntity`로 넣는다.

```java
httpPost.setHeader("Content-Type", "text/plain; charset=UTF-8");
httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
```

---

## 4) 패키지 구조와 책임

```text
src/main/java/com/example/eaimessage
├─ model
│  ├─ ChannelType
│  ├─ MessageType
│  ├─ MessageSendRequest
│  ├─ MessagePayload
│  ├─ HttpSendRequest
│  ├─ ATalkHeaderSendData
│  └─ ATalkBodySendData
├─ builder
│  ├─ MessageBuilder
│  ├─ AbstractMessageBuilder
│  ├─ ATalkMessageBuilder
│  ├─ MailMessageBuilder
│  └─ SmsMessageBuilder
├─ factory
│  └─ MessageBuilderFactory
├─ client
│  └─ EaiHttpClient
├─ service
│  └─ MessageSendService
└─ SampleMessageRunner
```

### model

- 요청/전송/전문 구성에 필요한 데이터 모델 정의
- `ATalkHeaderSendData`, `ATalkBodySendData`는 고정길이 문자열 생성 책임 포함

### builder

- 채널별 메시지 조립 규칙 구현
- `messageType` 분기 처리

### factory

- 채널 타입으로 적절한 Builder 선택

### service

- 전체 유스케이스 오케스트레이션
  - Builder 선택 -> payload 생성 -> HTTP 전송 요청 생성 -> 전송 실행

### client

- 실제 HTTP 호출 담당

---

## 5) 도메인 모델 설명

## 5.1 ChannelType

```java
ALIMTALK, MAIL, SMS
```

Factory가 Builder를 선택하는 기준.

## 5.2 MessageType

```java
ATALK_APPROVAL_REQUEST
ATALK_APPROVAL_COMPLETE
ATALK_INFO
MAIL_PERFORMANCE_REPORT
MAIL_NOTICE
SMS_NOTICE
```

각 채널 Builder 내부 switch 분기의 기준.

## 5.3 MessageSendRequest

공통 입력 DTO:

- `channelType`
- `messageType`
- `subject`, `content`
- `templateCode`, `senderKey`
- `recipients`
- `data` (치환용 부가 데이터)

---

## 6) 채널별 빌더 동작 상세

## 6.1 ATalkMessageBuilder

역할:

- 알림톡 본문 조립
- 알림톡 messageType 분기
- `ATalkHeaderSendData` + `ATalkBodySendData` 문자열 생성

분기 예시:

- `ATALK_APPROVAL_REQUEST`
  - templateCode: `APRV_REQ`
  - subject: `[승인요청] {approverName}`
  - content: `결재 문서번호: {documentNo}`
- `ATALK_APPROVAL_COMPLETE`
  - templateCode: `APRV_DONE`
  - subject: `[승인완료] {approverName}`
  - content: `결재 완료 ({approveDate})`
- `ATALK_INFO`
  - 요청값 또는 기본값 사용

## 6.2 MailMessageBuilder

역할:

- 메일 채널 본문/헤더 문자열 생성
- 메일 관련 messageType 분기 처리

지원:

- `MAIL_PERFORMANCE_REPORT`
- `MAIL_NOTICE`

## 6.3 SmsMessageBuilder

역할:

- SMS 채널 본문/헤더 문자열 생성
- SMS messageType 검증 및 값 세팅

지원:

- `SMS_NOTICE`

---

## 7) 전체 처리 시퀀스

1. 클라이언트가 `MessageSendService.send(request)` 호출
2. `MessageBuilderFactory.getBuilder(channelType)`로 Builder 선택
3. 선택된 Builder에서:
   - 요청 유효성 검증
   - `messageType` 분기 값 세팅
   - body 문자열 생성
   - header 문자열 생성
   - `header + body` 결합
4. `HttpSendRequest(url, payload)` 생성
5. `EaiHttpClient.send()` 호출
6. `text/plain; charset=UTF-8`으로 EAI 서버에 POST

---

## 8) 오류 처리 전략

- `channelType` 누락: `IllegalArgumentException`
- 채널/타입 불일치: `IllegalArgumentException`
- HTTP 상태코드 400 이상: `IllegalStateException`
- I/O 오류: `IllegalStateException`

현재는 간결한 예제 구현으로 런타임 예외를 사용했다.
운영 환경에서는 공통 예외 체계(에러코드/로깅/재시도)로 확장 가능하다.

---

## 9) 확장 가이드

신규 채널 추가 시 권장 순서:

1. `ChannelType`에 채널 추가
2. 채널용 Builder 1개 추가 (`AbstractMessageBuilder` 상속)
3. Builder 내부에서 `messageType` 분기 구현
4. `MessageBuilderFactory`에 매핑 추가

신규 메시지 타입 추가 시:

1. `MessageType` enum 확장
2. 해당 채널 Builder 내부 switch에 case 추가
3. 필요 데이터 키(`request.data`) 정의

중요:

- "메시지 타입마다 Builder 클래스를 추가"하는 방식은 지양
- "채널별 Builder 1개 + 내부 분기" 구조를 유지

---

## 10) 샘플 요청 실행

`SampleMessageRunner`는 `sample.run=true`일 때 동작한다.

예시:

```properties
sample.run=true
eai.endpoint=http://localhost:8081/eai/send
```

샘플은 알림톡 승인요청 메시지를 조립해 `MessageSendService`를 호출한다.

---

## 11) 현재 설계의 의도 요약

- **단순성**: 채널별 Builder 1개로 구조 과증식 방지
- **명확성**: 서비스/팩토리/빌더/클라이언트 책임 분리
- **현업 적합성**: JSON이 아닌 전문 문자열 송신 요구에 직접 대응
- **확장성**: 채널 추가/타입 추가 시 영향 범위 최소화

이 설계는 “복잡한 계층 추가 없이, 요구사항을 정확히 만족하는 최소 구조”를
목표로 한다.

