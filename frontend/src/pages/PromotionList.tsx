import React, { useEffect, useState } from 'react';
import type { Promotion } from '../types/promotion';
import type { MyPageResponse } from '../types/user';
import { promotionService } from '../services/promotionService';
import { userService } from '../services/userService';
import { ProductCard } from '../components/promotion/ProductCard';
import { useAuth } from '../hooks/useAuth';

export const PromotionList: React.FC = () => {
  const { user } = useAuth();
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [userInfo, setUserInfo] = useState<MyPageResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPromotions();
    if (user) {
      loadUserInfo();
    }
  }, [user]);

  const loadPromotions = async () => {
    try {
      setLoading(true);
      const data = await promotionService.getPromotions();
      setPromotions(data);
    } catch (err: any) {
      console.error('프로모션 로드 실패:', err);
      setError('프로모션 목록을 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const loadUserInfo = async () => {
    if (!user) return;
    try {
      const data = await userService.getUserProfile(user.id);
      // MyPageResponse를 생성 (total_saved 포함)
      const myPageData: MyPageResponse = {
        name: data.name,
        money: data.money,
        total_saved: 0, // 백엔드에서 제공하는 값 (임시로 0)
      };
      setUserInfo(myPageData);
    } catch (err: any) {
      console.error('사용자 정보 로드 실패:', err);
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-sale-red mx-auto mb-4"></div>
            <p className="text-text-meta">로딩 중...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="bg-sale-red bg-opacity-10 border border-sale-red rounded-lg p-6 text-center">
          <p className="text-sale-red">{error}</p>
          <button
            onClick={loadPromotions}
            className="mt-4 text-sm text-sale-red hover:underline"
          >
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-text-primary mb-2">
          ⏰ 타임딜
        </h1>
        <p className="text-text-secondary">
          한정된 시간, 특별한 가격! 놓치지 마세요
        </p>
      </div>

      {/* User Info - 절약 금액 표시 */}
      {user && userInfo && (
        <div className="mb-6 bg-gradient-to-r from-sale-red to-pink-600 rounded-lg p-6 text-white shadow-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm opacity-90 mb-1">{userInfo.name}님이 타임딜로 절약한 금액</p>
              <p className="text-3xl font-bold">{userInfo.total_saved.toLocaleString()}원</p>
            </div>
            <div className="text-5xl">💰</div>
          </div>
        </div>
      )}

      {/* Stats */}
      <div className="mb-6 flex items-center justify-between">
        <p className="text-text-secondary">
          총 <span className="font-bold text-sale-red">{promotions.length}</span>개의 타임딜 진행 중
        </p>
      </div>

      {/* Product Grid - 11st style: 3 columns on desktop */}
      {promotions.length === 0 ? (
        <div className="text-center py-16">
          <p className="text-text-meta text-lg">진행 중인 타임딜이 없습니다.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {promotions.map((promotion) => (
            <ProductCard key={promotion.id} promotion={promotion} />
          ))}
        </div>
      )}
    </div>
  );
};
