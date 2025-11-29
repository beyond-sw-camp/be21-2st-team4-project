import { api } from './api';
import { API_ENDPOINTS } from '../config/api';
import { handleApiResponse } from '../types/api';
import type { ApiResult } from '../types/api';
import type { QueueRequest, QueueResponse } from '../types/queue';

export const queueService = {
  /**
   * Enter queue for a promotion
   */
  async enterQueue(timedealId: number, userId: number): Promise<QueueResponse> {
    const request: QueueRequest = { timedealId, userId };
    const response = await api.post<ApiResult<QueueResponse>>(
      API_ENDPOINTS.QUEUE_ENTER,
      request
    );
    return handleApiResponse(response.data);
  },

  /**
   * Check queue status
   */
  async checkStatus(timedealId: number, userId: number): Promise<QueueResponse> {
    const response = await api.get<ApiResult<QueueResponse>>(
      API_ENDPOINTS.QUEUE_STATUS,
      {
        params: { timedealId, userId },
      }
    );
    return handleApiResponse(response.data);
  },
};
