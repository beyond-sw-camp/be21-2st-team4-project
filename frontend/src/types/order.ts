// Order Types (백엔드 API 스펙 기준)

// 백엔드 OrderRequest (POST /api/v1/orders)
export interface CreateOrderRequest {
  promotionId: number;
  quantity: number;
  userId: number;
}

// 백엔드 OrderResponse (POST /api/v1/orders 응답)
export interface OrderResponse {
  OrderId: number;      // 백엔드는 대문자 O
  quantity: number;
}

// 프론트엔드용 확장 Order 타입 (주문 목록 표시용)
export interface Order {
  id: number;
  userId: number;
  promotionId: number;
  productName: string;
  quantity: number;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
}

export type OrderStatus = 'DONE' | 'CANCELLED';

// 백엔드 MyPageOrderResponse (GET /api/v1/users/me/orders)
export interface MyPageOrderResponse {
  orderId: number;
  image: string;
  PromotionName: string;    // 백엔드는 대문자 P
  quantity: number;
  price: number;
  orderDate: string;        // LocalDateTime
}

// 백엔드 OrderDetailResponse (GET /api/v1/users/me/orders)
export interface OrderDetailResponse {
  myPageOrderResponseList: MyPageOrderResponse[];
}

export interface OrderDetail {
  id: number;
  orderId: number;
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}
