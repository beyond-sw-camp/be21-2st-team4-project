import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { userService } from '../services/userService';
import { useAuth } from '../hooks/useAuth';

export const AdminLogin = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      // Note: 백엔드에 관리자 전용 로그인 API가 없으므로 일반 로그인 사용
      const response = await userService.signIn(formData.email, formData.password);

      // SignInResponse를 User 형태로 변환 (임시)
      const user = {
        id: 1,
        email: formData.email,
        name: formData.email.split('@')[0],
        balance: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      login(user, response.token);
      navigate('/admin/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : '로그인에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-warning-orange to-warning-orange-alt flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            관리자 로그인
          </h1>
          <p className="text-gray-600">
            관리자 계정으로 로그인하세요
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded">
              {error}
            </div>
          )}

          <Input
            label="이메일"
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="admin@example.com"
            required
          />

          <Input
            label="비밀번호"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="비밀번호를 입력하세요"
            required
          />

          <Button
            type="submit"
            variant="secondary"
            fullWidth
            disabled={isLoading}
          >
            {isLoading ? '로그인 중...' : '관리자 로그인'}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <button
            onClick={() => navigate('/')}
            className="text-gray-600 hover:text-gray-900 text-sm"
          >
            ← 돌아가기
          </button>
        </div>
      </div>
    </div>
  );
};
