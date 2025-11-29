import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { QueueStatus } from '../components/queue/QueueStatus';
import { useQueue } from '../hooks/useQueue';
import { Button } from '../components/common';

export const Queue: React.FC = () => {
  const { promotionId } = useParams<{ promotionId: string }>();
  const navigate = useNavigate();

  // Get userId from localStorage (in production, use proper auth context)
  const userId = parseInt(localStorage.getItem('userId') || '0');

  const { queueData, loading, error, startPolling } = useQueue(
    promotionId ? parseInt(promotionId) : null,
    userId || null
  );

  useEffect(() => {
    if (!userId) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    if (promotionId && userId) {
      startPolling();
    }
  }, [promotionId, userId]);

  const handlePurchase = () => {
    navigate(`/checkout/${promotionId}`);
  };

  const handleRejoin = () => {
    window.location.reload();
  };

  if (!userId) {
    return null;
  }

  if (error) {
    return (
      <div className="min-h-screen bg-bg-gray flex items-center justify-center px-4">
        <div className="bg-white rounded-lg shadow-lg p-8 max-w-md text-center">
          <div className="text-5xl mb-4">⚠️</div>
          <h2 className="text-2xl font-bold text-sale-red mb-4">오류</h2>
          <p className="text-text-secondary mb-6">{error}</p>
          <Button variant="primary" onClick={() => navigate('/promotions')}>
            타임딜 목록으로
          </Button>
        </div>
      </div>
    );
  }

  if (loading || !queueData) {
    return (
      <div className="min-h-screen bg-bg-gray flex items-center justify-center px-4">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-sale-red mx-auto mb-4"></div>
          <p className="text-text-meta">큐 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-bg-gray flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-2xl">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            대기열 상태
          </h1>
          <p className="text-text-secondary">
            순서가 되면 구매하실 수 있습니다
          </p>
        </div>

        <QueueStatus
          queueData={queueData}
          onPurchase={handlePurchase}
          onRejoin={handleRejoin}
        />

        <div className="mt-6 text-center">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/promotions')}
          >
            타임딜 목록으로 돌아가기
          </Button>
        </div>
      </div>
    </div>
  );
};
