// API Response Types
export interface ApiResult<T> {
  result: 'SUCCESS' | 'ERROR';  // 백엔드는 'result' 사용 (not 'resultType')
  data: T | null;
  error: ErrorMessage | null;
}

export interface ErrorMessage {
  code: string;
  message: string;
}

// Helper function to handle API responses
export function handleApiResponse<T>(response: ApiResult<T>): T {
  if (response.result === 'SUCCESS' && response.data !== null) {
    return response.data;
  } else {
    throw new Error(response.error?.message || 'API Error');
  }
}
