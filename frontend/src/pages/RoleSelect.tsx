import { useNavigate } from 'react-router-dom';
import { Button } from '../components/common/Button';

export const RoleSelect = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-sale-red to-sale-red-alt flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            11번가 타임딜
          </h1>
          <p className="text-gray-600">
            로그인 유형을 선택해주세요
          </p>
        </div>

        <div className="space-y-4">
          <div className="border-2 border-gray-200 rounded-lg p-6 hover:border-primary-blue transition-colors">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              일반 회원
            </h2>
            <p className="text-gray-600 text-sm mb-4">
              타임딜 상품을 구매하고 주문을 관리합니다
            </p>
            <Button
              variant="primary"
              fullWidth
              onClick={() => navigate('/login')}
            >
              회원 로그인
            </Button>
          </div>

          <div className="border-2 border-gray-200 rounded-lg p-6 hover:border-warning-orange transition-colors">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              관리자
            </h2>
            <p className="text-gray-600 text-sm mb-4">
              상품과 프로모션을 관리합니다
            </p>
            <Button
              variant="secondary"
              fullWidth
              onClick={() => navigate('/admin/login')}
            >
              관리자 로그인
            </Button>
          </div>
        </div>

        <div className="mt-6 text-center">
          <p className="text-gray-600 text-sm">
            아직 계정이 없으신가요?
          </p>
          <button
            onClick={() => navigate('/signup')}
            className="text-primary-blue hover:underline text-sm font-medium mt-1"
          >
            회원가입 하기
          </button>
        </div>
      </div>
    </div>
  );
};
