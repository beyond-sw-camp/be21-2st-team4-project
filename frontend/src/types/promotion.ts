// Promotion Types (백엔드 PromotionResponse 스펙 기준)
export interface Promotion {
  // 백엔드 필수 필드
  id: number;
  adminId: number;
  productId: number;
  salePrice: number;        // 백엔드에서 제공
  discountRate: number;
  totalQuantity: number;
  startTime: string;
  endTime: string;
  productName: string;      // 백엔드에서 제공
  productImage: string;     // 백엔드에서 제공 (imageUrl)
  originalPrice: number;    // 백엔드에서 제공

  // 프론트엔드 전용 (계산 또는 임시)
  soldQuantity?: number;    // 백엔드에서 제공 안함 - 필요시 별도 API
  promotionStatus?: PromotionStatus;  // 백엔드에서 제공 안함 - 시간 기준 계산
}

// 백엔드 Enum: SCHEDULER, ACTIVE, ENDED
export type PromotionStatus = 'SCHEDULER' | 'ACTIVE' | 'ENDED';

export interface PromotionListResponse {
  promotions: Promotion[];
  total: number;
}

// 백엔드 PromotionRequest (POST/PUT)
export interface PromotionRequest {
  adminId: number;
  productId: number;
  discountRate: number;
  startTime: string;        // LocalDateTime
  endTime: string;          // LocalDateTime
  totalQuantity: number;
}
