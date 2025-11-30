import { api } from './api';
import { API_ENDPOINTS } from '../config/api';
import { handleApiResponse } from '../types/api';
import type { ApiResult } from '../types/api';
import type { User } from '../types/user';

export interface AdminLoginRequest {
  email: string;
  password: string;
}

export interface AdminLoginResponse {
  user: User;
  token: string;
}

export const adminService = {
  /**
   * Admin login
   */
  async login(credentials: AdminLoginRequest): Promise<AdminLoginResponse> {
    const response = await api.post<ApiResult<AdminLoginResponse>>(
      API_ENDPOINTS.ADMIN_LOGIN,
      credentials
    );
    return handleApiResponse(response.data);
  },

  /**
   * Get admin dashboard stats
   */
  async getDashboardStats(): Promise<any> {
    const response = await api.get<ApiResult<any>>(API_ENDPOINTS.ADMIN_DASHBOARD);
    return handleApiResponse(response.data);
  },
};
