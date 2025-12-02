import { useState, useEffect } from 'react';
import type { User } from '../types/user';
import { userService } from '../services/userService';

export const useAuth = () => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const userId = localStorage.getItem('userId');
    if (userId) {
      loadUser(parseInt(userId));
    } else {
      setLoading(false);
    }
  }, []);

  const loadUser = async (userId: number) => {
    try {
      const userData = await userService.getUserProfile(userId);
      setUser(userData);
    } catch (error) {
      console.error('Failed to load user:', error);
      localStorage.removeItem('userId');
      localStorage.removeItem('authToken');
    } finally {
      setLoading(false);
    }
  };

  const signIn = async (email: string, password: string) => {
    try {
      const response = await userService.signIn(email, password);

      // 토큰 저장
      localStorage.setItem('authToken', response.token);
      localStorage.setItem('userEmail', email);

      // 임시 사용자 객체 생성 (실제로는 토큰에서 디코딩하거나 별도 API로 조회)
      const userData: User = {
        id: 1, // 임시 ID
        email: email,
        name: email.split('@')[0], // 이메일에서 이름 추출
        balance: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      setUser(userData);
      localStorage.setItem('userId', '1'); // 임시

      return userData;
    } catch (error) {
      throw error;
    }
  };

  const signUp = async (email: string, password: string, name: string) => {
    try {
      await userService.signUp(email, password, name);
      // 회원가입 성공 후 자동 로그인은 하지 않음
      return;
    } catch (error) {
      throw error;
    }
  };

  const login = (user: User, token: string) => {
    setUser(user);
    localStorage.setItem('userId', user.id.toString());
    localStorage.setItem('authToken', token);
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('userId');
    localStorage.removeItem('authToken');
  };

  return {
    user,
    loading,
    signIn,
    signUp,
    login,
    logout,
  };
};
