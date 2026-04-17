# EAI Message Common Module

`ChannelType`(채널별 헤더/바디 템플릿)와 `MessageType`(메시지별 데이터 생성) 책임을 분리한 조합형 메시지 생성 모듈입니다.

이 모듈의 목적은 다음과 같습니다.

- 진입 서비스는 단순하게 유지
- 채널별 차이점은 `HeaderGenerator`, `DefaultBodyTemplate`에 집중
- 메시지 타입별 데이터 조회/조립은 `BodyGenerator`에 집중
- 공통 전송 흐름은 `MessageSendService` 한 곳에서 관리
- 새 메시지 타입 또는 새 채널 추가 시 기존 서비스 수정 범위를 최소화

---

## 1. 전체 아키텍처

### 핵심 흐름

```text
Caller
  -> TalkService.send(request)
  -> MessageSendService.send(request)
     -> BodyGeneratorFactory.get(channelType, messageType)
     -> EaiBodyGenerator.generate(request)
     -> BodyData 생성
     -> DefaultBodyTemplate.generate(bodyData)
     -> body 문자열 생성
     -> HeaderGeneratorFactory.get(channelType)
     -> EaiHeaderGenerator.generate(request, bodyData, bodyLength)
     -> HeaderData 생성
     -> DefaultHeaderTemplate.generate(headerData)
     -> header 문자열 생성
     -> finalMessage = header + body
     -> EaiProperties.resolveEndpoint(channelType)
     -> EaiHttpClient.send(HttpSendRequest)
```

### 설계 포인트

- `TalkService`는 기존 진입점 역할만 유지하고 실제 조립/전송은 `MessageSendService`에 위임합니다.
- `BodyGeneratorFactory`는 `ChannelType + MessageType` 조합으로 적절한 `EaiBodyGenerator`를 선택합니다.
- 각 `EaiBodyGenerator`는 프로젝트 내부 서비스 호출을 통해 제목/본문 등 실제 데이터를 조회하고 `BodyData`를 만듭니다.
- `DefaultBodyTemplate`는 채널별 전문 포맷에 맞춰 `BodyData`를 최종 body 문자열로 변환합니다.
- `HeaderGeneratorFactory`는 `ChannelType` 기준으로 적절한 `EaiHeaderGenerator`를 선택합니다.
- 각 `EaiHeaderGenerator`는 채널별 인터페이스 ID, 시스템 코드 등 헤더 값을 채워 `HeaderData`를 만듭니다.
- `DefaultHeaderTemplate`는 공통 헤더 포맷 규칙으로 `HeaderData`를 고정 길이 문자열로 변환합니다.
- 최종 전송은 `EaiHttpClient`가 담당합니다.

---

## 2. 실제 호출 구조

### 2-1. 진입점

#### `TalkService`
- 외부에서 가장 먼저 호출되는 서비스입니다.
- 역할은 최소화되어 있으며, 요청을 그대로 `MessageSendService`에 위임합니다.
- 기존 호출부와의 호환성을 유지하기 위한 파사드 성격입니다.

### 2-2. 메인 오케스트레이션

#### `MessageSendService`
가장 중요한 중심 서비스입니다.

처리 순서:

1. 요청값 검증
   - `channelType` null 체크
   - `messageType` null 체크
   - `receiverId` blank 체크
2. `BodyGeneratorFactory`에서 body generator 선택
3. 선택된 `EaiBodyGenerator`가 `BodyData` 생성
4. 채널에 맞는 `DefaultBodyTemplate`로 body 문자열 생성
5. body UTF-8 byte 길이 계산
6. `HeaderGeneratorFactory`에서 header generator 선택
7. 선택된 `EaiHeaderGenerator`가 `HeaderData` 생성
8. `DefaultHeaderTemplate`로 header 문자열 생성
9. `header + body` 결합
10. `EaiProperties`에서 채널별 endpoint 조회
11. `EaiHttpClient`로 최종 전문 전송

즉, 이 클래스는 **선택(factory) → 데이터 생성(generator) → 문자열 조립(template) → 전송(client)** 을 한 곳에서 관리합니다.

---

## 3. 패키지별 역할

### `model`
발송 요청, 채널/메시지 타입, 전송 요청 같은 기본 모델을 관리합니다.

### `service`
외부 진입 서비스, 전송 오케스트레이션, 메시지별 콘텐츠 조회 서비스를 관리합니다.

### `factory`
채널/메시지 타입에 맞는 generator를 찾아주는 선택 책임을 가집니다.

### `generator.header`
헤더용 데이터 생성과 헤더 전문 조립 책임을 가집니다.

### `generator.body`
바디용 데이터 생성과 바디 전문 조립 책임을 가집니다.

### `client`
외부 EAI endpoint로 전문을 실제 전송합니다.

### `config`
채널별 endpoint 등 환경설정을 관리합니다.

### `builder`
고정 길이 문자열 포맷팅 같은 공통 문자열 유틸리티를 제공합니다.

---

## 4. 파일별 기능 설명

아래는 현재 README 기준으로 파악되는 핵심 파일들의 역할입니다.

### 4-1. service 패키지

#### `TalkService`
- 외부 진입점
- 실제 비즈니스 처리는 하지 않음
- `MessageSendService.send(request)` 호출만 담당

#### `MessageSendService`
- 전체 발송 흐름 총괄
- 요청 검증
- body generator 선택 및 실행
- body template 조립
- header generator 선택 및 실행
- header template 조립
- endpoint 조회
- HTTP client 전송

#### `AMessageContentService`
- `A_DOCUMENT` 유형 메시지의 제목/본문 원천 데이터 제공
- 현재는 샘플 문자열을 반환하지만, 실제 구조에서는 프로젝트 내부 다른 서비스/쿼리 호출로 대체될 수 있음

#### `BMessageContentService`
- `B_DOCUMENT` 유형 메시지의 제목/본문 원천 데이터 제공
- 역할은 `AMessageContentService`와 동일하며 메시지 타입만 다름

### 4-2. factory 패키지

#### `HeaderGeneratorFactory`
- `ChannelType -> EaiHeaderGenerator` 매핑 관리
- 같은 채널에 generator가 중복 등록되면 예외 발생
- 채널별 헤더 생성기 선택 전담

#### `BodyGeneratorFactory`
- `ChannelType -> MessageType -> EaiBodyGenerator` 2단 매핑 관리
- 같은 채널/메시지타입 조합이 중복되면 예외 발생
- 채널+메시지타입 기준 바디 생성기 선택 전담

### 4-3. generator.header 패키지

#### `EaiHeaderGenerator`
- 채널별 헤더 데이터 생성 인터페이스
- `supportChannelType()`과 `generate(...)` 계약 제공

#### `AtalkHeaderGenerator`
- `A_TALK` 채널용 헤더 데이터 생성기
- 시스템 코드 `ATALKSYS`
- 채널 인터페이스 ID, transaction id, title/content 등을 `HeaderData`에 채움

#### `EmailHeaderGenerator`
- `EMAIL` 채널용 헤더 데이터 생성기
- 시스템 코드 `EMAILSYS`
- 이메일 채널에 맞는 헤더 값을 `HeaderData`에 채움

#### `HeaderData`
- 헤더 조립에 필요한 값 묶음
- `systemCode`, `interfaceId`, `transactionId`, `channelType`, `messageType`, `bodyLength`, `title`, `content` 보유

#### `DefaultHeaderTemplate`
- `HeaderData`를 실제 고정 길이 header 문자열로 조립
- 길이 규칙과 padding 규칙이 이 클래스에 모여 있음
- `FixedLengthFieldFormatter`를 사용해 우측 padding 수행
- body 길이는 0-padding 숫자로 변환

### 4-4. generator.body 패키지

#### `EaiBodyGenerator`
- 채널+메시지타입별 바디 데이터 생성 인터페이스
- `supportChannelType()`, `supportMessageType()`, `generate(request)` 계약 제공

#### `ADocumentBodyGenerator`
- `A_TALK + A_DOCUMENT` 조합 처리
- `AMessageContentService` 호출
- 결과를 `BodyData`로 변환

#### `BDocumentBodyGenerator`
- `A_TALK + B_DOCUMENT` 조합 처리
- `BMessageContentService` 호출
- 결과를 `BodyData`로 변환

#### `EmailADocumentBodyGenerator`
- `EMAIL + A_DOCUMENT` 조합 처리
- `AMessageContentService` 호출
- 결과를 `BodyData`로 변환

#### `EmailBDocumentBodyGenerator`
- `EMAIL + B_DOCUMENT` 조합 처리
- `BMessageContentService` 호출
- 결과를 `BodyData`로 변환

#### `BodyData`
- 바디 조립용 데이터 묶음
- `messageType`, `receiverId`, `title`, `content` 보유

#### `DefaultBodyTemplate`
- 채널별 body 템플릿 인터페이스
- `supportChannelType()`과 `generate(BodyData)` 계약 제공

#### `ATalkDefaultBodyTemplate`
- `A_TALK` 채널용 body 문자열 조립
- `messageType`, `receiverId`, `title`, `content`를 고정 길이 문자열로 조립

#### `EmailDefaultBodyTemplate`
- `EMAIL` 채널용 body 문자열 조립
- 현재 필드 길이 구조는 `ATalkDefaultBodyTemplate`와 동일하나, 채널별 전문 포맷 차이를 별도 클래스로 분리해 둔 구조

### 4-5. model 패키지

#### `TalkRequest`
- 발송 요청 DTO
- 최소 필드:
  - `channelType`
  - `messageType`
  - `receiverId`
  - `data`
- `getString(key)`를 통해 null-safe 문자열 조회 가능
- 향후 부가 파라미터를 유연하게 실어 나를 수 있도록 `Map<String, Object>` 지원

#### `ChannelType`
- 채널 enum
- 현재 값:
  - `A_TALK`
  - `EMAIL`
- 각 채널은 `channelInterfaceId`를 가짐

#### `MessageType`
- 메시지 타입 enum
- 현재 값:
  - `A_DOCUMENT`
  - `B_DOCUMENT`

#### `HttpSendRequest`
- `EaiHttpClient`가 실제 전송 시 사용하는 요청 모델
- 일반적으로 endpoint URL과 최종 전문 문자열을 담는 용도

### 4-6. config / client / builder 패키지

#### `EaiProperties`
- 채널별 endpoint 관리
- `resolveEndpoint(channelType)` 형태로 채널별 전송 URL 반환
- 외부 설정값과 전송 모듈을 연결하는 역할

#### `EaiHttpClient`
- 최종 전문을 외부 EAI 서버로 전송
- `HttpSendRequest`를 받아 HTTP POST 등 실제 네트워크 호출 수행

#### `FixedLengthFieldFormatter`
- 문자열 우측 padding 등 고정 길이 전문 포맷 유틸리티
- header/body 템플릿에서 반복되는 포맷 로직 공통화

#### `SampleTalkRunner`
- 샘플 실행 또는 데모용 러너
- 애플리케이션 실행 시 테스트 요청을 만드는 예제가 들어갈 수 있는 위치

---

## 5. 데이터 흐름 예시

예를 들어 아래 요청이 들어왔다고 가정합니다.

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.EMAIL)
    .messageType(MessageType.A_DOCUMENT)
    .receiverId("user-1001")
    .build();
```

이때 호출 흐름은 다음과 같습니다.

1. `TalkService.send(request)` 호출
2. `MessageSendService.send(request)` 진입
3. `BodyGeneratorFactory.get(EMAIL, A_DOCUMENT)` 호출
4. `EmailADocumentBodyGenerator` 선택
5. `AMessageContentService`에서 title/content 조회
6. `BodyData` 생성
7. `EmailDefaultBodyTemplate.generate(bodyData)` 호출
8. body 문자열 생성
9. body byte length 계산
10. `HeaderGeneratorFactory.get(EMAIL)` 호출
11. `EmailHeaderGenerator` 선택
12. `HeaderData` 생성
13. `DefaultHeaderTemplate.generate(headerData)` 호출
14. header 문자열 생성
15. `finalMessage = header + body`
16. `EaiProperties.resolveEndpoint(EMAIL)` 호출
17. `EaiHttpClient.send(new HttpSendRequest(url, finalMessage))`

---

## 6. 현재 구조의 장점

### 6-1. 책임 분리가 명확함
- 메시지 원천 데이터 생성: `EaiBodyGenerator`
- 채널별 body 포맷: `DefaultBodyTemplate`
- 채널별 header 값 생성: `EaiHeaderGenerator`
- 공통 header 포맷: `DefaultHeaderTemplate`
- 전체 흐름 제어: `MessageSendService`

### 6-2. 확장성이 좋음
- 새 메시지 타입 추가 시 기존 서비스 흐름을 거의 수정하지 않아도 됨
- 새 채널 추가 시 채널별 구현만 추가하면 됨
- factory가 자동으로 bean 목록을 수집하므로 조합 구조를 유지하기 쉬움

### 6-3. 유지보수 포인트가 분리됨
- 포맷 변경은 template 계층
- 데이터 조회 변경은 content service / body generator 계층
- 채널별 헤더 정책 변경은 header generator 계층

---

## 7. 확장 방법

### 새 메시지 타입 추가
예: `C_DOCUMENT` 추가

1. `MessageType` enum에 `C_DOCUMENT` 추가
2. 채널별로 필요한 `EaiBodyGenerator` 구현 추가
   - `A_TALK` 지원 시: `CDocumentBodyGenerator`
   - `EMAIL` 지원 시: `EmailCDocumentBodyGenerator`
3. 필요한 콘텐츠 조회 서비스 추가 또는 기존 서비스 재사용
4. `MessageSendService` 수정 없이 사용 가능

### 새 채널 추가
예: `SMS` 추가

1. `ChannelType` enum에 `SMS` 추가
2. `SmsHeaderGenerator` 추가
3. `SmsDefaultBodyTemplate` 추가
4. 지원할 각 메시지 타입에 대한 body generator 추가
   - `SmsADocumentBodyGenerator`
   - `SmsBDocumentBodyGenerator`
5. `EaiProperties`에 SMS endpoint 설정 추가
6. `MessageSendService` 수정 없이 사용 가능

---

## 8. 주의할 점

- 같은 `ChannelType`에 `EaiHeaderGenerator`가 2개 등록되면 애플리케이션 시작 시 예외가 발생합니다.
- 같은 `ChannelType + MessageType` 조합에 `EaiBodyGenerator`가 2개 등록되면 애플리케이션 시작 시 예외가 발생합니다.
- `DefaultBodyTemplate`도 채널별로 1개만 존재해야 합니다.
- `receiverId`, `messageType`, `channelType`는 필수값입니다.
- `DefaultHeaderTemplate`, `DefaultBodyTemplate`의 필드 길이 변경은 외부 연계 전문 스펙 영향이 있으므로 주의해야 합니다.

---

## 9. 현재 모델 요약

### ChannelType
- `A_TALK` (`channelInterfaceId`: `ATK0001`)
- `EMAIL` (`channelInterfaceId`: `EML0001`)

### MessageType
- `A_DOCUMENT`
- `B_DOCUMENT`

### TalkRequest
- `channelType`
- `messageType`
- `receiverId`
- `data`

---

## 10. 샘플 요청

```java
TalkRequest request = TalkRequest.builder()
    .channelType(ChannelType.EMAIL)
    .messageType(MessageType.A_DOCUMENT)
    .receiverId("user-1001")
    .build();

talkService.send(request);
```

---

## 11. 한 줄 정리

이 모듈은 **채널별 포맷 책임**과 **메시지 타입별 데이터 생성 책임**을 분리하고, `MessageSendService`가 이를 조합해 최종 EAI 전문을 전송하는 구조입니다.
