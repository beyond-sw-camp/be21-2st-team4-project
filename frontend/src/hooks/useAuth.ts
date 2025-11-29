import { useState, useEffect } from 'react';
import { User } from '../types/user';
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
      setUser(response.user);
      localStorage.setItem('userId', response.user.id.toString());
      if (response.token) {
        localStorage.setItem('authToken', response.token);
      }
      return response.user;
    } catch (error) {
      throw error;
    }
  };

  const signUp = async (email: string, password: string, name: string) => {
    try {
      const user = await userService.signUp(email, password, name);
      return user;
    } catch (error) {
      throw error;
    }
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
    logout,
  };
};
