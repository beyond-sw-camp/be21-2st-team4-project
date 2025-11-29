import { api } from './api';
import { API_ENDPOINTS } from '../config/api';
import { handleApiResponse } from '../types/api';
import type { ApiResult } from '../types/api';
import type { User, SignInRequest, SignInResponse, SignUpRequest } from '../types/user';
import type { Order } from '../types/order';

export const userService = {
  /**
   * Sign in user
   */
  async signIn(email: string, password: string): Promise<SignInResponse> {
    const request: SignInRequest = { email, password };
    const response = await api.post<ApiResult<SignInResponse>>(
      API_ENDPOINTS.USER_SIGNIN,
      request
    );
    return handleApiResponse(response.data);
  },

  /**
   * Sign up new user
   */
  async signUp(email: string, password: string, name: string): Promise<User> {
    const request: SignUpRequest = { email, password, name };
    const response = await api.post<ApiResult<User>>(
      API_ENDPOINTS.USER_SIGNUP,
      request
    );
    return handleApiResponse(response.data);
  },

  /**
   * Get user profile
   */
  async getUserProfile(userId: number): Promise<User> {
    const response = await api.get<ApiResult<User>>(API_ENDPOINTS.USER_ME, {
      params: { userId },
    });
    return handleApiResponse(response.data);
  },

  /**
   * Get user order history
   */
  async getOrderHistory(userId: number): Promise<Order[]> {
    const response = await api.get<ApiResult<Order[]>>(
      API_ENDPOINTS.USER_ORDERS,
      {
        params: { userId },
      }
    );
    return handleApiResponse(response.data);
  },
};
