# 백엔드-프론트엔드 API 동기화 보고서

> **작성일**: 2025-12-04
> **작업**: TD-13 프론트엔드 타입을 백엔드 API 스펙에 맞춤

---

## ✅ 완료된 수정 사항

### 1. User 타입 수정
**변경 내용**: `balance` → `money`

**이유**: 백엔드 MyPageResponse가 `money` 필드 사용

**수정 파일**:
- `frontend/src/types/user.ts`
- `frontend/src/components/layout/Header.tsx`

```typescript
// Before
interface User {
  balance: number;
}

// After
interface User {
  money: number;  // 백엔드 스펙에 맞춤
}
```

---

### 2. Promotion 타입 수정
**변경 내용**:
- `PromotionStatus` enum 수정: `INACTIVE` → `SCHEDULER`
- 필수 필드 추가: `salePrice`, `productName`, `productImage`, `originalPrice`
- `PromotionRequest` 백엔드 스펙 추가

**이유**: 백엔드 PromotionResponse 스펙에 맞춤

**수정 파일**:
- `frontend/src/types/promotion.ts`
- `frontend/src/services/promotionService.ts`

```typescript
// Enum 변경
export type PromotionStatus = 'SCHEDULER' | 'ACTIVE' | 'ENDED';

// Request 백엔드 스펙
export interface PromotionRequest {
  adminId: number;
  productId: number;
  discountRate: number;
  startTime: string;
  endTime: string;
  totalQuantity: number;
}
```

---

### 3. Order 타입 수정
**변경 내용**:
- `OrderResponse` 추가: 백엔드는 `{ OrderId, quantity }` 만 반환
- `MyPageOrderResponse` 추가: 주문 내역 조회 응답
- `OrderDetailResponse` 구조 변경

**이유**: 백엔드 주문 API 응답 구조에 맞춤

**수정 파일**:
- `frontend/src/types/order.ts`
- `frontend/src/services/orderService.ts`
- `frontend/src/pages/Checkout.tsx`
- `frontend/src/pages/Orders.tsx`

```typescript
// 백엔드 OrderResponse (POST /api/v1/orders)
export interface OrderResponse {
  OrderId: number;  // 대문자 O 주의!
  quantity: number;
}

// 백엔드 MyPageOrderResponse (GET /api/v1/users/me/orders)
export interface MyPageOrderResponse {
  orderId: number;
  image: string;
  PromotionName: string;  // 대문자 P 주의!
  quantity: number;
  price: number;
  orderDate: string;
}

// 백엔드 OrderDetailResponse
export interface OrderDetailResponse {
  myPageOrderResponseList: MyPageOrderResponse[];
}
```

---

### 4. Queue 타입 수정
**변경 내용**: `waitTime` → `waitingTime`

**이유**: 백엔드는 `waitingTime` 필드 사용

**수정 파일**:
- `frontend/src/types/queue.ts`

```typescript
// Before
export interface QueueResponse {
  waitTime?: number;
}

// After
export interface QueueResponse {
  waitingTime: number;  // 백엔드 스펙에 맞춤 (초 단위)
}
```

---

## ⚠️ 주의사항 (백엔드 필드명 특이사항)

### 대소문자 혼용
백엔드에서 일부 필드명이 일반적인 camelCase가 아닌 특이한 형태를 사용합니다:

1. **OrderResponse.OrderId** - 대문자 O
2. **MyPageOrderResponse.PromotionName** - 대문자 P
3. **MyPageResponse.total_saved** - snake_case

프론트엔드에서는 백엔드 응답을 그대로 사용하도록 타입을 정의했습니다.

---

## 🚨 여전히 남아있는 불일치 (백엔드 수정 권장)

### 1. 프로모션 상태 조회 API 경로 오타
**파일**: `PromotionController.java:49`
```java
// 현재 (오타)
@GetMapping("/api/v1/promtions/{promotionStatus}")

// 수정 필요
@GetMapping("/api/v1/promotions/{promotionStatus}")
```

### 2. SignInResponse에 사용자 정보 부족
**문제**: 로그인 시 `token`만 반환되어 사용자 정보를 알 수 없음

**현재 백엔드**:
```java
record SignInResponse(String token)
```

**권장 수정**:
```java
record SignInResponse(
  String token,
  Long userId,
  String email,
  String name,
  Integer money
)
```

**영향**: 현재 프론트엔드에서 로그인 후 사용자 정보를 하드코딩하거나 추가 API 호출 필요

### 3. 프로모션 상세 조회 API 반환 타입
**파일**: `PromotionController.java:56`

**문제**: 엔티티를 직접 반환
```java
return ApiResult.success(promotion);  // Promotion 엔티티
```

**권장**: DTO 사용
```java
return ApiResult.success(PromotionResponse.from(promotion));
```

### 4. 주문 생성 응답 정보 부족
**문제**: OrderResponse가 `{ OrderId, quantity }`만 반환하여 주문 완료 페이지에서 추가 정보 부족

**권장 추가 필드**:
- productName
- totalPrice
- orderDate

---

## 📊 백엔드 API 엔드포인트 정리

### Promotion API
| 메서드 | 경로 | Request | Response |
|--------|------|---------|----------|
| GET | `/api/v1/promotions` | - | `List<PromotionResponse>` |
| GET | `/api/v1/promotions/{id}` | - | `Promotion` (엔티티) |
| GET | `/api/v1/promotions/{status}` | - | `List<PromotionResponse>` |
| POST | `/api/v1/promotions` | PromotionRequest | - |
| PUT | `/api/v1/promotions/{id}` | PromotionRequest | - |
| DELETE | `/api/v1/promotions/{id}` | - | - |

### Order API
| 메서드 | 경로 | Request | Response |
|--------|------|---------|----------|
| POST | `/api/v1/orders` | OrderRequest | OrderResponse |

### Queue API
| 메서드 | 경로 | Request | Response |
|--------|------|---------|----------|
| POST | `/api/v1/queue` | QueueEnterRequest | QueueResponse |
| GET | `/api/v1/queue` | ?timedealId&userId | QueueResponse |
| DELETE | `/api/v1/queue` | ?timedealId&userId | Boolean |

### User API
| 메서드 | 경로 | Request | Response |
|--------|------|---------|----------|
| POST | `/api/v1/users/signIn` | LoginRequest | SignInResponse |
| POST | `/api/v1/users/signUp` | SignUpRequest | - |
| POST | `/api/v1/users/signOut` | ?userId | - |
| GET | `/api/v1/users/me` | ?userId | MyPageResponse |
| GET | `/api/v1/users/me/orders` | ?userId | OrderDetailResponse |

---

## 🔄 프론트엔드 적응 전략

백엔드 수정이 어려운 경우, 프론트엔드에서 다음과 같이 대응했습니다:

### 1. 부족한 정보 처리
- `soldQuantity`: 프론트엔드에서 옵셔널로 처리, 기본값 0 사용
- `promotionStatus`: 시간 비교로 프론트엔드에서 계산

### 2. 하드코딩된 부분
- AdminLogin에서 로그인 후 사용자 정보를 임의 생성 (임시 대응)
- 주문 완료 페이지에서 URL 파라미터로 정보 전달

### 3. 타입 변환
- 백엔드 응답을 받은 후 프론트엔드 타입으로 변환
- MyPageOrderResponse → Order 형태로 표시

---

## ✨ 권장사항

### 백엔드 개선 우선순위

**🔴 긴급**:
1. PromotionController Line 49 오타 수정 (`promtions` → `promotions`)
2. SignInResponse에 사용자 정보 추가

**🟡 높음**:
3. OrderResponse 확장 (productName, totalPrice 추가)
4. PromotionResponse에 soldQuantity, promotionStatus 추가

**🟢 중간**:
5. 필드명 일관성 (camelCase 통일)
6. ApiResult 래퍼 일관성 (CategoryController)

---

## 📝 변경 이력

| 날짜 | 작업 | 담당 |
|------|------|------|
| 2025-12-04 | 프론트엔드 타입 전면 수정 (백엔드 스펙 기준) | Claude Code |
| 2025-12-04 | Orders, Checkout 페이지 백엔드 응답 처리 수정 | Claude Code |
| 2025-12-04 | API 동기화 보고서 작성 | Claude Code |

---

**최종 업데이트**: 2025-12-04
**상태**: ✅ 프론트엔드 백엔드 스펙 동기화 완료
