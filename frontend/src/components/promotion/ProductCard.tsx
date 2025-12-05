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

  // ENDED 상태 프로모션은 렌더링하지 않음
  if (promotion.promotionStatus === 'ENDED') {
    return null;
  }

  // 백엔드에서 상품 정보를 제공하지 않으므로 기본값 사용
  const soldQuantity = promotion.soldQuantity || 0;
  const remainingQuantity = promotion.totalQuantity - soldQuantity;
  const isSoldOut = remainingQuantity === 0;
  const stockPercentage = (remainingQuantity / promotion.totalQuantity) * 100;
  const isLowStock = stockPercentage > 0 && stockPercentage <= 20;

  // 임시 상품 정보 (실제로는 Product API를 별도로 조회해야 함)
  const productName = promotion.productName || `상품 #${promotion.productId}`;
  const productImage = promotion.productImage;
  const originalPrice = promotion.originalPrice || 0;
  const salePrice = promotion.salePrice || Math.round(originalPrice * (1 - promotion.discountRate / 100));

  // 상태별 타이머 시간 결정
  const timerEndTime = promotion.promotionStatus === 'SCHEDULER'
    ? promotion.startTime  // SCHEDULER: 시작까지 남은 시간
    : promotion.endTime;    // ACTIVE: 끝까지 남은 시간

  const handleClick = () => {
    if (!isSoldOut && promotion.promotionStatus !== 'SCHEDULER') {
      navigate(`/promotions/${promotion.id}`);
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
        {!isSoldOut && promotion.promotionStatus !== 'SCHEDULER' && (
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
        {promotion.promotionStatus === 'SCHEDULER' && (
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
          {!isSoldOut && isLowStock && promotion.promotionStatus === 'ACTIVE' && (
            <Badge variant="limited">한정수량</Badge>
          )}
          {promotion.promotionStatus === 'ACTIVE' && !isSoldOut && (
            <Badge variant="new">진행중</Badge>
          )}
          {promotion.promotionStatus === 'SCHEDULER' && (
            <Badge variant="new">대기중</Badge>
          )}
        </div>

        {/* Timer Badge (bottom right) - ACTIVE 상태만 표시 */}
        {!isSoldOut && promotion.promotionStatus === 'ACTIVE' && (
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
            <div className="flex items-center text-primary-blue">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
              </svg>
              <span className="text-xs">상세보기</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
