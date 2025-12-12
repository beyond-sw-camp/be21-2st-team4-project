import { api } from './api';
import { API_ENDPOINTS } from '../config/api';
import { handleApiResponse } from '../types/api';
import type { ApiResult } from '../types/api';
import type { QueueResponse } from '../types/queue';

export const queueService = {
  /**
   * Enter queue for a promotion
   * userId는 JWT 토큰에서 자동 추출됨
   * Gateway RewritePath 패턴 매칭을 위해 trailing slash 필요
   */
  async enterQueue(timedealId: number): Promise<QueueResponse> {
    const response = await api.post<ApiResult<QueueResponse>>(
      `${API_ENDPOINTS.QUEUE_ENTER}/`,
      null,
      { params: { timedealId } }
    );
    return handleApiResponse(response.data);
  },

  /**
   * Check queue status
   * 백엔드에서 userId를 query parameter로 요구함
   */
  async checkStatus(timedealId: number): Promise<QueueResponse> {
    const userId = localStorage.getItem('userId');
    const response = await api.get<ApiResult<QueueResponse>>(
      `${API_ENDPOINTS.QUEUE_STATUS}/`,
      { params: { timedealId, userId } }
    );
    return handleApiResponse(response.data);
  },

  /**
   * Leave queue
   * userId는 JWT 토큰에서 자동 추출됨
   */
  async leaveQueue(timedealId: number): Promise<boolean> {
    const response = await api.delete<ApiResult<boolean>>(
      `${API_ENDPOINTS.QUEUE_ENTER}/`,
      { params: { timedealId } }
    );
    return handleApiResponse(response.data);
  },

  /**
   * Verify queue (check if user can proceed to checkout)
   * userId는 JWT 토큰에서 자동 추출됨
   */
  async verifyQueue(timedealId: number): Promise<void> {
    const response = await api.get<ApiResult<void>>(
      `${API_ENDPOINTS.QUEUE_STATUS}/verify`,
      { params: { timedealId } }
    );
    return handleApiResponse(response.data);
  },
};
