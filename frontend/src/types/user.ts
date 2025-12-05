// User Types (백엔드 API 스펙 기준)
export interface User {
  id: number;
  email: string;
  name: string;
  money: number;  // 백엔드는 'money' 사용 (balance 아님)
  createdAt: string;
  updatedAt: string;
}

export interface SignInRequest {
  email: string;
  password: string;
}

export interface SignUpRequest {
  email: string;
  password: string;
  name: string;
}

export interface SignInResponse {
  userId: number;  // 백엔드에서 사용자 ID 반환
  token: string;
}

// 백엔드 MyPageResponse (GET /api/v1/users/me)
export interface MyPageResponse {
  name: string;
  money: number;
  total_saved: number;  // 백엔드는 snake_case 사용
}
