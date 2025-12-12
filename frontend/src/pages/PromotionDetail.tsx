import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { Promotion } from '../types/promotion';
import { promotionService } from '../services/promotionService';
import { queueService } from '../services/queueService';
import { useAuth } from '../contexts/AuthContext';
import { CountdownTimer } from '../components/promotion/CountdownTimer';
import { Badge, Button } from '../components/common';

export const PromotionDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [promotion, setPromotion] = useState<Promotion | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [isJoiningQueue, setIsJoiningQueue] = useState(false);

  useEffect(() => {
    if (id) {
      loadPromotion(parseInt(id));
    }
  }, [id]);

  const loadPromotion = async (promotionId: number) => {
    try {
      setLoading(true);
      const data = await promotionService.getPromotionById(promotionId);
      setPromotion(data);
      setError('');
    } catch (err: any) {
      console.error('프로모션 로드 실패:', err);
      setError('프로모션 정보를 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleJoinQueue = async () => {
    if (!user || !promotion) return;

    try {
      setIsJoiningQueue(true);
      // userId는 JWT 토큰에서 자동 추출됨
      await queueService.enterQueue(promotion.id);
      navigate(`/queue/${promotion.id}`);
    } catch (err: any) {
      console.error('대기열 입장 실패:', err);
      alert('대기열 입장에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsJoiningQueue(false);
    }
  };

  const handleQuantityChange = (delta: number) => {
    const newQuantity = quantity + delta;
    if (newQuantity >= 1 && newQuantity <= 10) {
      setQuantity(newQuantity);
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

  if (error || !promotion) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="bg-sale-red bg-opacity-10 border border-sale-red rounded-lg p-6 text-center">
          <p className="text-sale-red">{error || '프로모션을 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/promotions')} className="mt-4">
            목록으로 돌아가기
          </Button>
        </div>
      </div>
    );
  }

  const soldQuantity = promotion.soldQuantity || 0;
  const remainingQuantity = promotion.totalQuantity - soldQuantity;
  const isSoldOut = remainingQuantity <= 0;
  const stockPercentage = (remainingQuantity / promotion.totalQuantity) * 100;
  const isLowStock = stockPercentage > 0 && stockPercentage <= 20;

  const productName = promotion.productName || `상품 #${promotion.id}`;
  const productImage = promotion.productImageUrl || promotion.productImage;
  const originalPrice = promotion.originalPrice || 0;
  const salePrice = promotion.salePrice || Math.round(originalPrice * (1 - promotion.discountRate / 100));

  // 현재 시간 기준으로 상태 계산
  const now = new Date();
  const startTime = new Date(promotion.startTime);
  const endTime = new Date(promotion.endTime);

  const isPromotionActive = promotion.promotionStatus === 'ACTIVE' ||
    (!promotion.promotionStatus && now >= startTime && now <= endTime);
  const isBeforeStart = now < startTime;
  const isAfterEnd = now > endTime;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Breadcrumb */}
      <div className="mb-6 text-sm text-text-meta">
        <button onClick={() => navigate('/promotions')} className="hover:text-primary-blue">
          타임딜
        </button>
        <span className="mx-2">&gt;</span>
        <span className="text-text-primary">{productName}</span>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Left: Product Image */}
        <div>
          <div className="relative aspect-square bg-bg-gray rounded-lg overflow-hidden">
            {productImage ? (
              <img
                src={productImage}
                alt={productName}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-text-meta">
                <svg className="w-32 h-32" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            )}

            {/* Sold Out Overlay */}
            {isSoldOut && (
              <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
                <div className="bg-badge-gray text-white px-8 py-4 rounded-lg font-bold text-2xl">
                  품절
                </div>
              </div>
            )}

            {/* Badges */}
            <div className="absolute top-4 left-4 flex flex-col gap-2">
              {!isSoldOut && isLowStock && <Badge variant="limited">한정수량</Badge>}
              {isPromotionActive && !isSoldOut && <Badge variant="new">진행중</Badge>}
            </div>
          </div>
        </div>

        {/* Right: Product Info & Purchase */}
        <div>
          {/* Title */}
          <h1 className="text-3xl font-bold text-text-primary mb-4">
            {productName}
          </h1>

          {/* Timer */}
          {!isSoldOut && !isAfterEnd && (
            <div className="bg-gradient-to-r from-sale-red to-red-600 text-white p-4 rounded-lg mb-6">
              <div className="flex items-center justify-between">
                <span className="text-lg font-bold">⏰ 타임딜 마감까지</span>
                <CountdownTimer endTime={promotion.endTime} variant="large" />
              </div>
            </div>
          )}

          {/* Status Messages */}
          {isBeforeStart && (
            <div className="bg-warning-orange bg-opacity-10 border border-warning-orange text-warning-orange p-4 rounded-lg mb-6">
              <p className="font-bold">타임딜이 곧 시작됩니다!</p>
              <p className="text-sm mt-1">
                시작 시간: {new Date(promotion.startTime).toLocaleString('ko-KR')}
              </p>
            </div>
          )}

          {isAfterEnd && (
            <div className="bg-badge-gray bg-opacity-10 border border-badge-gray text-badge-gray p-4 rounded-lg mb-6">
              <p className="font-bold">타임딜이 종료되었습니다</p>
            </div>
          )}

          {/* Price */}
          <div className="border-t border-b border-border-default py-6 mb-6">
            <div className="flex items-center gap-4 mb-2">
              <span className="text-5xl font-bold text-sale-red">
                {promotion.discountRate}%
              </span>
              <div>
                <div className="text-3xl font-bold text-text-primary">
                  {salePrice.toLocaleString()}원
                </div>
                {originalPrice > 0 && (
                  <div className="text-lg text-text-meta line-through">
                    {originalPrice.toLocaleString()}원
                  </div>
                )}
              </div>
            </div>

            {originalPrice > 0 && (
              <div className="text-success-green text-lg font-bold">
                {(originalPrice - salePrice).toLocaleString()}원 할인
              </div>
            )}
          </div>

          {/* Stock Info */}
          <div className="bg-bg-gray p-4 rounded-lg mb-6">
            <div className="flex items-center justify-between mb-2">
              <span className="text-text-secondary">재고 현황</span>
              <span className="font-bold text-text-primary">
                {remainingQuantity} / {promotion.totalQuantity}
              </span>
            </div>
            <div className="w-full bg-border-default rounded-full h-3 overflow-hidden">
              <div
                className={`h-full transition-all ${
                  isLowStock ? 'bg-sale-red' : 'bg-success-green'
                }`}
                style={{ width: `${stockPercentage}%` }}
              />
            </div>
            {isLowStock && !isSoldOut && (
              <p className="text-sale-red text-sm mt-2 font-bold">
                ⚠ 재고가 얼마 남지 않았습니다!
              </p>
            )}
          </div>

          {/* Quantity Selector */}
          {!isSoldOut && !isAfterEnd && (
            <div className="mb-6">
              <label className="block text-text-secondary mb-2">수량</label>
              <div className="flex items-center gap-4">
                <button
                  onClick={() => handleQuantityChange(-1)}
                  disabled={quantity <= 1}
                  className="w-10 h-10 border border-border-default rounded hover:border-sale-red disabled:opacity-30 disabled:cursor-not-allowed"
                >
                  -
                </button>
                <span className="text-xl font-bold w-12 text-center">{quantity}</span>
                <button
                  onClick={() => handleQuantityChange(1)}
                  disabled={quantity >= 10 || quantity >= remainingQuantity}
                  className="w-10 h-10 border border-border-default rounded hover:border-sale-red disabled:opacity-30 disabled:cursor-not-allowed"
                >
                  +
                </button>
                <span className="text-text-meta text-sm ml-4">
                  (최대 10개 / 재고 {remainingQuantity}개)
                </span>
              </div>
            </div>
          )}

          {/* Total Price */}
          {!isSoldOut && !isAfterEnd && (
            <div className="bg-bg-light p-4 rounded-lg mb-6">
              <div className="flex items-center justify-between text-lg">
                <span className="text-text-secondary">총 상품 금액</span>
                <span className="text-2xl font-bold text-sale-red">
                  {(salePrice * quantity).toLocaleString()}원
                </span>
              </div>
            </div>
          )}

          {/* Action Buttons */}
          <div className="space-y-3">
            {isSoldOut ? (
              <Button disabled fullWidth size="large">
                품절
              </Button>
            ) : isAfterEnd ? (
              <Button disabled fullWidth size="large">
                타임딜 종료
              </Button>
            ) : isBeforeStart ? (
              <Button disabled fullWidth size="large">
                타임딜 대기 중
              </Button>
            ) : (
              <>
                <Button
                  variant="primary"
                  fullWidth
                  size="large"
                  onClick={handleJoinQueue}
                  disabled={isJoiningQueue}
                >
                  {isJoiningQueue ? '대기열 입장 중...' : '🎯 구매하기 (대기열 입장)'}
                </Button>
                <p className="text-sm text-text-meta text-center">
                  대기열에 입장하여 순서대로 구매하실 수 있습니다
                </p>
              </>
            )}
          </div>

          {/* Promotion Info */}
          <div className="mt-8 pt-8 border-t border-border-default">
            <h3 className="font-bold text-text-primary mb-4">프로모션 정보</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-text-meta">프로모션 ID</span>
                <span className="text-text-primary">#{promotion.id}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-meta">시작 시간</span>
                <span className="text-text-primary">
                  {new Date(promotion.startTime).toLocaleString('ko-KR')}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-meta">종료 시간</span>
                <span className="text-text-primary">
                  {new Date(promotion.endTime).toLocaleString('ko-KR')}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-text-meta">할인율</span>
                <span className="text-sale-red font-bold">{promotion.discountRate}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Additional Info Section */}
      <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-bg-light p-6 rounded-lg text-center">
          <div className="text-4xl mb-2">🚚</div>
          <h4 className="font-bold text-text-primary mb-2">빠른 배송</h4>
          <p className="text-sm text-text-secondary">
            주문 후 영업일 기준 2-3일 내 배송
          </p>
        </div>
        <div className="bg-bg-light p-6 rounded-lg text-center">
          <div className="text-4xl mb-2">💯</div>
          <h4 className="font-bold text-text-primary mb-2">품질 보증</h4>
          <p className="text-sm text-text-secondary">
            정품 보증 및 불량 시 100% 환불
          </p>
        </div>
        <div className="bg-bg-light p-6 rounded-lg text-center">
          <div className="text-4xl mb-2">🔒</div>
          <h4 className="font-bold text-text-primary mb-2">안전 결제</h4>
          <p className="text-sm text-text-secondary">
            안전한 결제 시스템으로 보호
          </p>
        </div>
      </div>
    </div>
  );
};
