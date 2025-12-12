import React from 'react';
import { useNavigate } from 'react-router-dom';
import type { Promotion } from '../../types/promotion';
import { Badge } from '../common';
import { CountdownTimer } from './CountdownTimer';

export interface ProductCardProps {
  promotion: Promotion;
}

export const ProductCard: React.FC<ProductCardProps> = ({ promotion }) => {
  const navigate = useNavigate();

  // 현재 시간 기준으로 상태 계산
  const now = new Date();
  const startTime = new Date(promotion.startTime);
  const endTime = new Date(promotion.endTime);

  // promotionStatus가 없으면 시간으로 계산
  const status = promotion.promotionStatus || (
    now < startTime ? 'SCHEDULER' :
    now > endTime ? 'ENDED' : 'ACTIVE'
  );

  // ENDED 상태 프로모션은 렌더링하지 않음
  if (status === 'ENDED') {
    return null;
  }

  // 백엔드에서 soldQuantity 제공 (없으면 0)
  const soldQuantity = promotion.soldQuantity || 0;
  const remainingQuantity = promotion.totalQuantity - soldQuantity;
  const isSoldOut = remainingQuantity <= 0;
  const stockPercentage = (remainingQuantity / promotion.totalQuantity) * 100;
  const isLowStock = stockPercentage > 0 && stockPercentage <= 20;

  // 상품 정보 (백엔드 필드명 호환)
  const productName = promotion.productName || `상품 #${promotion.id}`;
  const productImage = promotion.productImageUrl || promotion.productImage;
  const originalPrice = promotion.originalPrice || 0;
  const salePrice = promotion.salePrice || Math.round(originalPrice * (1 - promotion.discountRate / 100));

  // 상태별 타이머 시간 결정
  const timerEndTime = status === 'SCHEDULER'
    ? promotion.startTime  // SCHEDULER: 시작까지 남은 시간
    : promotion.endTime;    // ACTIVE: 끝까지 남은 시간

  const handleClick = () => {
    if (!isSoldOut && status !== 'SCHEDULER') {
      // 프로모션 클릭 시 바로 대기열 입장
      navigate(`/queue/${promotion.id}`);
    }
  };

  return (
    <div
      className={`product-card group relative cursor-pointer ${isSoldOut ? 'opacity-60' : ''}`}
      onClick={handleClick}
    >
      {/* Product Image */}
      <div className="relative aspect-[2/1] overflow-hidden bg-bg-gray">
        {productImage ? (
          <img
            src={productImage}
            alt={productName}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-text-meta">
            <svg className="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        )}

        {/* Timer Overlay (shown on hover) */}
        {!isSoldOut && status !== 'SCHEDULER' && (
          <CountdownTimer
            endTime={timerEndTime}
            variant="overlay"
          />
        )}

        {/* Sold Out Overlay */}
        {isSoldOut && (
          <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-badge-gray text-white px-4 py-2 rounded-lg font-bold text-lg">
              품절
            </div>
          </div>
        )}

        {/* Scheduler Overlay */}
        {status === 'SCHEDULER' && (
          <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
            <div className="text-center text-white">
              <div className="text-sm mb-2">곧 시작합니다</div>
              <CountdownTimer
                endTime={timerEndTime}
                variant="badge"
              />
            </div>
          </div>
        )}

        {/* Badges */}
        <div className="absolute top-2 left-2 flex flex-wrap gap-1">
          {!isSoldOut && isLowStock && status === 'ACTIVE' && (
            <Badge variant="limited">한정수량</Badge>
          )}
          {status === 'ACTIVE' && !isSoldOut && (
            <Badge variant="new">진행중</Badge>
          )}
          {status === 'SCHEDULER' && (
            <Badge variant="new">대기중</Badge>
          )}
        </div>

        {/* Timer Badge (bottom right) - ACTIVE 상태만 표시 */}
        {!isSoldOut && status === 'ACTIVE' && (
          <div className="absolute bottom-2 right-2">
            <CountdownTimer
              endTime={timerEndTime}
              variant="badge"
            />
          </div>
        )}
      </div>

      {/* Product Info */}
      <div className="p-4">
        {/* Product Name */}
        <h3 className="text-product-name text-text-primary mb-2 line-clamp-2 min-h-[3rem]">
          {productName}
        </h3>

        {/* Price Section */}
        <div className="flex items-baseline gap-2 mb-2">
          <span className="price-discount">{promotion.discountRate}%</span>
          <span className="price-sale">{salePrice.toLocaleString()}원</span>
        </div>

        {originalPrice > 0 && (
          <div className="price-original mb-3">
            {originalPrice.toLocaleString()}원
          </div>
        )}

        {/* Stock Info */}
        <div className="flex items-center justify-between text-sm">
          <span className="text-text-meta">
            재고: {remainingQuantity} / {promotion.totalQuantity}
          </span>
          {!isSoldOut && (
            <div className="flex items-center text-sale-red">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.707l-3-3a1 1 0 00-1.414 1.414L10.586 9H7a1 1 0 100 2h3.586l-1.293 1.293a1 1 0 101.414 1.414l3-3a1 1 0 000-1.414z" clipRule="evenodd" />
              </svg>
              <span className="text-xs font-medium">입장하기</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
