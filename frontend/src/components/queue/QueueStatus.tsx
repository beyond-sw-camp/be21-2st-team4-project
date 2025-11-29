import React from 'react';
import { QueueResponse } from '../../types/queue';
import { Badge, Button } from '../common';
import { useCountdown } from '../../hooks/useCountdown';

export interface QueueStatusProps {
  queueData: QueueResponse;
  onPurchase?: () => void;
  onRejoin?: () => void;
  loading?: boolean;
}

export const QueueStatus: React.FC<QueueStatusProps> = ({
  queueData,
  onPurchase,
  onRejoin,
  loading = false,
}) => {
  const proceedTimer = useCountdown(queueData.proceedExpiryTime || new Date());

  const getStatusColor = () => {
    switch (queueData.status) {
      case 'WAITING':
        return 'warning-orange';
      case 'PROCEED':
        return 'success-green';
      case 'EXPIRED':
        return 'sale-red';
      default:
        return 'badge-gray';
    }
  };

  const getStatusText = () => {
    switch (queueData.status) {
      case 'WAITING':
        return '대기 중';
      case 'PROCEED':
        return '구매 가능';
      case 'EXPIRED':
        return '만료됨';
      default:
        return '알 수 없음';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg p-8 max-w-md mx-auto">
      {/* Status Badge */}
      <div className="text-center mb-6">
        <Badge
          variant="custom"
          backgroundColor={`var(--${getStatusColor()})`}
          color="white"
          className="text-lg px-4 py-2"
        >
          {getStatusText()}
        </Badge>
      </div>

      {/* Queue Position */}
      {queueData.status === 'WAITING' && (
        <div className="text-center mb-6">
          <div className="text-6xl font-bold text-sale-red mb-2 animate-pulse">
            #{queueData.position}
          </div>
          <p className="text-text-secondary">현재 대기 순번</p>
          {queueData.waitTime && (
            <p className="text-sm text-text-meta mt-2">
              예상 대기 시간: 약 {Math.ceil(queueData.waitTime / 60)}분
            </p>
          )}
        </div>
      )}

      {/* Proceed Status */}
      {queueData.status === 'PROCEED' && (
        <div className="text-center mb-6">
          <div className="text-5xl mb-4">🎉</div>
          <h3 className="text-2xl font-bold text-success-green mb-2">
            구매 가능!
          </h3>
          <p className="text-text-secondary mb-4">
            지금 바로 구매하실 수 있습니다
          </p>

          {queueData.proceedExpiryTime && !proceedTimer.isExpired && (
            <div className="bg-warning-orange bg-opacity-10 border border-warning-orange rounded-lg p-4">
              <p className="text-sm text-text-secondary mb-1">남은 시간</p>
              <div className="text-3xl font-bold text-warning-orange font-number">
                {String(proceedTimer.minutes).padStart(2, '0')}:{String(proceedTimer.seconds).padStart(2, '0')}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Expired Status */}
      {queueData.status === 'EXPIRED' && (
        <div className="text-center mb-6">
          <div className="text-5xl mb-4">⏰</div>
          <h3 className="text-2xl font-bold text-sale-red mb-2">
            시간 만료
          </h3>
          <p className="text-text-secondary">
            구매 가능 시간이 만료되었습니다
          </p>
        </div>
      )}

      {/* Progress Bar (for waiting) */}
      {queueData.status === 'WAITING' && (
        <div className="mb-6">
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className="bg-sale-red h-2 rounded-full transition-all duration-500"
              style={{ width: `${Math.min((queueData.position / 100) * 100, 100)}%` }}
            />
          </div>
        </div>
      )}

      {/* Action Buttons */}
      <div className="space-y-3">
        {queueData.status === 'PROCEED' && onPurchase && (
          <Button
            variant="primary"
            fullWidth
            size="lg"
            onClick={onPurchase}
            loading={loading}
            className="animate-pulse"
          >
            지금 구매하기
          </Button>
        )}

        {queueData.status === 'EXPIRED' && onRejoin && (
          <Button
            variant="primary"
            fullWidth
            size="lg"
            onClick={onRejoin}
            loading={loading}
          >
            다시 참가하기
          </Button>
        )}

        {queueData.status === 'WAITING' && (
          <Button
            variant="outline"
            fullWidth
            disabled
          >
            대기 중...
          </Button>
        )}
      </div>

      {/* Auto Refresh Indicator */}
      <div className="mt-6 text-center text-xs text-text-meta">
        <div className="flex items-center justify-center gap-1">
          <div className="w-2 h-2 bg-success-green rounded-full animate-pulse" />
          <span>3초마다 자동 업데이트</span>
        </div>
      </div>
    </div>
  );
};
