import React, { useEffect, useState } from 'react';
import type { Promotion } from '../types/promotion';
import { promotionService } from '../services/promotionService';
import { ProductCard } from '../components/promotion/ProductCard';

export const PromotionList: React.FC = () => {
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPromotions();
  }, []);

  const loadPromotions = async () => {
    try {
      setLoading(true);
      const data = await promotionService.getPromotions();
      setPromotions(data);
    } catch (err: any) {
      console.error('프로모션 로드 실패:', err);

      // 백엔드 API 문제로 인한 임시 처리
      // 백엔드 PromotionController 50번 라인: "api/v1/promotions" → "/api/v1/promotions" 수정 필요
      setError(
        '프로모션 목록을 불러올 수 없습니다.\n' +
        '(백엔드 API 경로 확인 필요: GET /api/v1/promotions)'
      );

      // 임시 목 데이터 사용 (개발용)
      // setPromotions([]);
    } finally {
      setLoading(false);
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

      {/* Stats */}
      <div className="mb-6 flex items-center justify-between">
        <p className="text-text-secondary">
          총 <span className="font-bold text-sale-red">{promotions.length}</span>개의 타임딜 진행 중
        </p>
        {/*
        <div className="flex gap-2">
          <button className="px-4 py-2 border border-border-default rounded hover:border-sale-red transition-colors">
            신상품순
          </button>
          <button className="px-4 py-2 border border-border-default rounded hover:border-sale-red transition-colors">
            마감임박순
          </button>
          <button className="px-4 py-2 border border-border-default rounded hover:border-sale-red transition-colors">
            할인율순
          </button>
        </div>
        */}
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
