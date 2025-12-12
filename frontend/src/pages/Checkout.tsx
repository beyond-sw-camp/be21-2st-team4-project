import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { Promotion } from '../types/promotion';
import { promotionService } from '../services/promotionService';
import { orderService } from '../services/orderService';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '../components/common';

export const Checkout: React.FC = () => {
  const { promotionId } = useParams<{ promotionId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [promotion, setPromotion] = useState<Promotion | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    if (promotionId) {
      loadPromotion(parseInt(promotionId));
    }
  }, [promotionId]);

  const loadPromotion = async (id: number) => {
    try {
      setLoading(true);
      const data = await promotionService.getPromotionById(id);
      setPromotion(data);
      setError('');
    } catch (err: any) {
      console.error('프로모션 로드 실패:', err);
      setError('프로모션 정보를 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuantityChange = (delta: number) => {
    const newQuantity = quantity + delta;
    if (promotion) {
      const remainingQuantity = promotion.totalQuantity - (promotion.soldQuantity || 0);
      if (newQuantity >= 1 && newQuantity <= 10 && newQuantity <= remainingQuantity) {
        setQuantity(newQuantity);
      }
    }
  };

  const handlePurchase = async () => {
    if (!user || !promotion) return;

    try {
      setIsProcessing(true);

      // 주문 생성 API 호출
      // 백엔드 응답: { OrderId: number, quantity: number }
      const orderResponse = await orderService.createOrder(
        user.id,
        promotion.id,
        quantity
      );

      // 주문 완료 페이지로 이동 (주문 정보 전달)
      const productName = promotion.productName || `상품 #${promotion.productId}`;
      const salePrice = promotion.salePrice || Math.round((promotion.originalPrice || 0) * (1 - promotion.discountRate / 100));
      const totalPrice = salePrice * quantity;

      navigate(`/order-complete?orderId=${orderResponse.OrderId}&productName=${encodeURIComponent(productName)}&quantity=${quantity}&totalPrice=${totalPrice}`);
    } catch (err: any) {
      console.error('주문 생성 실패:', err);

      // 에러 처리
      const errorMessage = err.response?.data?.error?.message || err.message || '주문에 실패했습니다.';
      alert(`구매 실패: ${errorMessage}`);
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8">
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
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="bg-sale-red bg-opacity-10 border border-sale-red rounded-lg p-6 text-center">
          <p className="text-sale-red mb-4">{error || '프로모션을 찾을 수 없습니다.'}</p>
          <Button onClick={() => navigate('/promotions')}>
            타임딜 목록으로
          </Button>
        </div>
      </div>
    );
  }

  const soldQuantity = promotion.soldQuantity || 0;
  const remainingQuantity = promotion.totalQuantity - soldQuantity;
  const isSoldOut = remainingQuantity === 0;

  const productName = promotion.productName || `상품 #${promotion.productId}`;
  const productImage = promotion.productImage;
  const originalPrice = promotion.originalPrice || 0;
  const salePrice = promotion.salePrice || Math.round(originalPrice * (1 - promotion.discountRate / 100));
  const totalPrice = salePrice * quantity;
  // 할인 금액은 양수로 표시 (절대값 사용)
  const totalDiscount = Math.abs((originalPrice - salePrice) * quantity);

  if (isSoldOut) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="bg-badge-gray bg-opacity-10 border border-badge-gray rounded-lg p-6 text-center">
          <div className="text-5xl mb-4">😢</div>
          <h2 className="text-2xl font-bold text-text-primary mb-2">품절되었습니다</h2>
          <p className="text-text-secondary mb-4">
            죄송합니다. 해당 상품은 품절되었습니다.
          </p>
          <Button onClick={() => navigate('/promotions')}>
            다른 타임딜 보러가기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-text-primary mb-2">
          🎯 구매하기
        </h1>
        <p className="text-text-secondary">
          대기열을 통과하셨습니다! 지금 바로 구매하세요.
        </p>
      </div>

      {/* Warning Banner */}
      <div className="bg-warning-orange bg-opacity-10 border border-warning-orange rounded-lg p-4 mb-6">
        <div className="flex items-start gap-3">
          <div className="text-2xl">⏰</div>
          <div>
            <h3 className="font-bold text-warning-orange mb-1">
              5분 내에 구매를 완료해주세요!
            </h3>
            <p className="text-sm text-text-secondary">
              시간이 초과되면 대기열로 다시 돌아가게 됩니다.
            </p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Product Info (2/3) */}
        <div className="lg:col-span-2 space-y-6">
          {/* Product Card */}
          <div className="bg-white border border-border-default rounded-lg overflow-hidden">
            <div className="bg-bg-light px-6 py-4 border-b border-border-default">
              <h2 className="text-lg font-bold text-text-primary">주문 상품</h2>
            </div>
            <div className="p-6">
              <div className="flex gap-4">
                {/* Product Image */}
                <div className="flex-shrink-0 w-32 h-32 bg-bg-gray rounded-lg overflow-hidden">
                  {productImage ? (
                    <img
                      src={productImage}
                      alt={productName}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-text-meta">
                      <svg className="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                  )}
                </div>

                {/* Product Info */}
                <div className="flex-1">
                  <h3 className="text-lg font-bold text-text-primary mb-2">
                    {productName}
                  </h3>
                  <div className="flex items-center gap-2 mb-2">
                    <span className="text-2xl font-bold text-sale-red">
                      {promotion.discountRate}%
                    </span>
                    <span className="text-xl font-bold text-text-primary">
                      {salePrice.toLocaleString()}원
                    </span>
                  </div>
                  {originalPrice > 0 && (
                    <div className="text-sm text-text-meta line-through">
                      {originalPrice.toLocaleString()}원
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Quantity Selector */}
          <div className="bg-white border border-border-default rounded-lg overflow-hidden">
            <div className="bg-bg-light px-6 py-4 border-b border-border-default">
              <h2 className="text-lg font-bold text-text-primary">수량 선택</h2>
            </div>
            <div className="p-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <button
                    onClick={() => handleQuantityChange(-1)}
                    disabled={quantity <= 1}
                    className="w-12 h-12 border-2 border-border-default rounded-lg hover:border-sale-red disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-xl font-bold"
                  >
                    -
                  </button>
                  <span className="text-2xl font-bold w-16 text-center">{quantity}</span>
                  <button
                    onClick={() => handleQuantityChange(1)}
                    disabled={quantity >= 10 || quantity >= remainingQuantity}
                    className="w-12 h-12 border-2 border-border-default rounded-lg hover:border-sale-red disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-xl font-bold"
                  >
                    +
                  </button>
                </div>
                <div className="text-right">
                  <div className="text-sm text-text-meta mb-1">
                    남은 재고: {remainingQuantity}개
                  </div>
                  <div className="text-xs text-text-meta">
                    (최대 구매 수량: 10개)
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Order Summary (1/3) */}
        <div className="lg:col-span-1">
          <div className="bg-white border border-border-default rounded-lg overflow-hidden sticky top-4">
            <div className="bg-bg-light px-6 py-4 border-b border-border-default">
              <h2 className="text-lg font-bold text-text-primary">결제 정보</h2>
            </div>
            <div className="p-6 space-y-4">
              {/* Price Breakdown */}
              <div className="space-y-3 pb-4 border-b border-border-default">
                <div className="flex justify-between text-sm">
                  <span className="text-text-meta">상품 금액</span>
                  <span className="text-text-primary">
                    {(originalPrice * quantity).toLocaleString()}원
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-text-meta">할인 금액</span>
                  <span className="text-sale-red font-bold">
                    -{totalDiscount.toLocaleString()}원
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-text-meta">배송비</span>
                  <span className="text-success-green font-bold">무료</span>
                </div>
              </div>

              {/* Total */}
              <div className="flex justify-between items-center pt-2">
                <span className="text-lg font-bold text-text-primary">최종 결제 금액</span>
              </div>
              <div className="text-right">
                <div className="text-3xl font-bold text-sale-red">
                  {totalPrice.toLocaleString()}원
                </div>
              </div>

              {/* Purchase Button */}
              <Button
                variant="primary"
                fullWidth
                size="large"
                onClick={handlePurchase}
                disabled={isProcessing}
              >
                {isProcessing ? '구매 처리 중...' : `${totalPrice.toLocaleString()}원 결제하기`}
              </Button>

              {/* Info */}
              <div className="bg-bg-light p-4 rounded-lg text-xs text-text-meta">
                <p>• 타임딜 상품은 한정 수량입니다</p>
                <p>• 주문 후 취소는 제한될 수 있습니다</p>
                <p>• 배송은 영업일 기준 2-3일 소요됩니다</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
