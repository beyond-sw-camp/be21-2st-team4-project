// Queue Types (백엔드 API 스펙 기준)

// 백엔드 QueueEnterRequest (POST /api/v1/queue)
export interface QueueRequest {
  timedealId: number;
  userId: number;
}

// 백엔드 QueueResponse
export interface QueueResponse {
  position: number;         // @PositiveOrZero Long position
  waitingTime: number;      // @PositiveOrZero Long waitingTime (초 단위) - 백엔드는 waitingTime 사용!
  status: QueueStatus;      // @NotNull QueueStatus
}

export type QueueStatus = 'WAITING' | 'PROCEED' | 'EXPIRED';
