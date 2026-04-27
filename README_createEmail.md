# createEmail - MGCARD_USAGE_REPORT 이메일 콘텐츠 생성 가이드

## 목적
마지막 커밋에서 추가된 카드 이용 통계 조회 쿼리(`PerformanceMapper.selectCardEvtStatByUserId`) 결과를 사용해,
`EMAIL` 채널의 UMS BODY를 `MGCARD_USAGE_REPORT` 타입으로 생성합니다.

## 동작 방식
1. `receiverId` 값을 사용자 ID로 사용해 성능 조회 쿼리를 수행합니다.
2. 쿼리 결과(`List<Map<String, Object>>`)를 플랫파일 텍스트로 변환합니다.
3. 데이터 구분자는 아래 규칙을 사용합니다.
   - 컬럼 구분자: `|`
   - 행 구분자: `@`

## 콘텐츠 포맷
```text
컬럼1|컬럼2|컬럼3@값11|값12|값13@값21|값22|값23
```

- 첫 번째 행은 컬럼 헤더입니다.
- 이후 행은 데이터 행입니다.
- 값 안에 `|`, `@`가 포함되면 공백으로 치환됩니다.
- 조회 데이터가 없으면 `NO_DATA`를 본문으로 반환합니다.

## 관련 클래스
- `MessageType.MGCARD_USAGE_REPORT`
- `EmailMgCardUsageReportBodyGenerator`
- `MgCardUsageReportContentService`
- `PerformanceService` / `PerformanceMapper`

## 샘플
- Title: `MGCARD_USAGE_REPORT`
- Content: `카드ID|카드명|...@CARD001|테스트카드|...`
