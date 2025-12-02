# 백엔드 API 문제점 정리

## 🚨 긴급 수정 필요

### 1. PromotionController - GetMapping 경로 오류

**파일:** `src/main/java/com/ohgiraffers/timedeal/core/api/controller/v1/PromotionController.java`

#### 문제 1: 슬래시(/) 누락 (50번 라인)
```java
@GetMapping("api/v1/promotions")  // ❌ 잘못됨
public ApiResult<List<PromotionResponse>> getAllPromotion() {
    return ApiResult.success(promotionService.findAll());
}
```

**수정:**
```java
@GetMapping("/api/v1/promotions")  // ✅ 슬래시 추가
public ApiResult<List<PromotionResponse>> getAllPromotion() {
    return ApiResult.success(promotionService.findAll());
}
```

#### 문제 2: 오타 promtions → promotions (54번 라인)
```java
@GetMapping("api/v1/promtions/{promotionStatus}")  // ❌ promtions (오타)
public ApiResult<List<PromotionResponse>> getPromotionsStatusAll(
        @PathVariable PromotionStatus promotionStatus)
{
    return ApiResult.success(promotionService.getPromotionsByStatus(promotionStatus));
}
```

**수정:**
```java
@GetMapping("/api/v1/promotions/{promotionStatus}")  // ✅ promotions (정확한 철자)
public ApiResult<List<PromotionResponse>> getPromotionsStatusAll(
        @PathVariable PromotionStatus promotionStatus)
{
    return ApiResult.success(promotionService.getPromotionsByStatus(promotionStatus));
}
```

---

## 📊 영향도

### 현재 상황
1. **프론트엔드 로그인**: ✅ 정상 동작
2. **프로모션 목록 조회**: ❌ 404 Not Found
   - 요청: `GET http://localhost:8080/api/v1/promotions`
   - 실제 백엔드 경로: `api/v1/promotions` (슬래시 없음)
   - 결과: 404 에러 → "API Error" 표시

### 사용자 경험
- 로그인 성공 후 타임딜 페이지(`/promotions`)로 이동
- 프로모션 목록을 로드하려고 시도
- **"프로모션 목록을 불러올 수 없습니다"** 에러 메시지 표시
- 페이지는 렌더링되지만 데이터가 없음

---

## 🔧 수정 방법

### 백엔드 수정 (권장)
```bash
# PromotionController.java 파일 수정
# 50번 라인: "api/v1/promotions" → "/api/v1/promotions"
# 54번 라인: "api/v1/promtions" → "/api/v1/promotions"
```

### 프론트엔드 임시 대응 (현재 적용됨)
```typescript
// PromotionList.tsx에서 에러 메시지 명확히 표시
setError(
  '프로모션 목록을 불러올 수 없습니다.\n' +
  '(백엔드 API 경로 확인 필요: GET /api/v1/promotions)'
);
```

---

## ✅ 테스트 방법

### 백엔드 수정 후 확인
1. 백엔드 재시작
2. Swagger UI 접속: http://localhost:8080/swagger-ui/
3. `GET /api/v1/promotions` 엔드포인트 확인
4. 프론트엔드에서 로그인 후 타임딜 페이지 확인

### API 직접 테스트
```bash
# 현재 (404 발생)
curl http://localhost:8080/api/v1/promotions

# 수정 후 (200 OK)
curl http://localhost:8080/api/v1/promotions
```

---

## 📝 관련 이슈

### 추가로 확인 필요한 사항
1. **프로모션 상세 조회 API**
   - `GET /api/v1/promotions/{id}` - 현재 구현되지 않음
   - PromotionController에 추가 필요

2. **프로모션 Response DTO**
   - `PromotionResponse`가 프론트엔드 `Promotion` 타입과 일치하는지 확인 필요
   - 필드명 매칭 (camelCase vs snake_case)

---

## 🎯 우선순위

1. **[긴급]** PromotionController 경로 수정 (슬래시 추가, 오타 수정)
2. **[높음]** 프로모션 상세 조회 API 구현
3. **[중간]** Response DTO 필드명 확인

---

**작성일:** 2025-11-30
**작성자:** Claude (Frontend Developer)
**확인자:** 백엔드 팀 확인 요망
