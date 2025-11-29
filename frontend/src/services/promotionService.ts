import { api } from './api';
import { API_ENDPOINTS } from '../config/api';
import { handleApiResponse } from '../types/api';
import type { ApiResult } from '../types/api';
import type { Promotion } from '../types/promotion';

export const promotionService = {
  /**
   * Get all active promotions
   */
  async getPromotions(): Promise<Promotion[]> {
    const response = await api.get<ApiResult<Promotion[]>>(
      API_ENDPOINTS.PROMOTIONS
    );
    return handleApiResponse(response.data);
  },

  /**
   * Get promotion details by ID
   */
  async getPromotionById(id: number): Promise<Promotion> {
    const response = await api.get<ApiResult<Promotion>>(
      `${API_ENDPOINTS.PROMOTION_DETAIL}/${id}`
    );
    return handleApiResponse(response.data);
  },
};
