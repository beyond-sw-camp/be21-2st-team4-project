import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { MyPageOrderResponse } from '../types/order';
import { orderService } from '../services/orderService';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '../components/common';

export const Orders: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [orders, setOrders] = useState<MyPageOrderResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user) {
      loadOrders();
    }
  }, [user]);

  const loadOrders = async () => {
    if (!user) return;

    try {
      setLoading(true);
      // 백엔드 응답: { myPageOrderResponseList: [...] }
      // userId는 토큰에서 자동 추출됨
      const data = await orderService.getUserOrders();
      // 최신순으로 정렬
      const orderList = data.myPageOrderResponseList || [];
      setOrders(orderList.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()));
      setError('');
    } catch (err: any) {
      console.error('주문 내역 로드 실패:', err);
      setError('주문 내역을 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 백엔드에는 status 필드가 없음 - 모든 주문은 완료 상태로 표시
  const getStatusBadge = () => {
    return (
      <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-bold bg-success-green bg-opacity-10 text-success-green">
        완료
      </span>
    );
  };

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-sale-red mx-auto mb-4"></div>
            <p className="text-text-meta">주문 내역을 불러오는 중...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="bg-sale-red bg-opacity-10 border border-sale-red rounded-lg p-6 text-center">
          <p className="text-sale-red mb-4">{error}</p>
          <Button onClick={loadOrders}>다시 시도</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-text-primary mb-2">
          주문 내역
        </h1>
        <p className="text-text-secondary">
          타임딜에서 구매한 상품들을 확인하세요
        </p>
      </div>

      {/* Stats */}
      {orders.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-white border border-border-default rounded-lg p-6 text-center">
            <div className="text-3xl font-bold text-primary-blue mb-2">
              {orders.length}
            </div>
            <div className="text-text-meta text-sm">총 주문 수</div>
          </div>
          <div className="bg-white border border-border-default rounded-lg p-6 text-center">
            <div className="text-3xl font-bold text-success-green mb-2">
              {orders.length}
            </div>
            <div className="text-text-meta text-sm">완료된 주문</div>
          </div>
          <div className="bg-white border border-border-default rounded-lg p-6 text-center">
            <div className="text-3xl font-bold text-sale-red mb-2">
              {orders.reduce((sum, order) => sum + order.price, 0).toLocaleString()}원
            </div>
            <div className="text-text-meta text-sm">총 구매 금액</div>
          </div>
        </div>
      )}

      {/* Orders List */}
      {orders.length === 0 ? (
        <div className="bg-white border border-border-default rounded-lg p-12 text-center">
          <div className="text-6xl mb-4">📦</div>
          <h2 className="text-2xl font-bold text-text-primary mb-2">
            주문 내역이 없습니다
          </h2>
          <p className="text-text-secondary mb-6">
            타임딜에서 특별한 상품을 만나보세요!
          </p>
          <Button variant="primary" onClick={() => navigate('/promotions')}>
            타임딜 보러가기
          </Button>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div
              key={order.orderId}
              className="bg-white border border-border-default rounded-lg overflow-hidden hover:border-primary-blue transition-colors"
            >
              <div className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="text-lg font-bold text-text-primary">
                        {order.PromotionName}
                      </h3>
                      {getStatusBadge()}
                    </div>
                    <div className="text-sm text-text-meta space-y-1">
                      <p>주문 번호: #{order.orderId.toString().padStart(8, '0')}</p>
                      <p>주문 일시: {new Date(order.orderDate).toLocaleString('ko-KR')}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-2xl font-bold text-sale-red mb-1">
                      {order.price.toLocaleString()}원
                    </div>
                    <div className="text-sm text-text-meta">
                      {order.quantity}개
                    </div>
                  </div>
                </div>

                {/* Additional Info */}
                <div className="flex items-center justify-between pt-4 border-t border-border-default">
                  <div className="flex items-center gap-2 text-sm text-text-meta">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>배송 준비 중</span>
                  </div>
                  {order.image && (
                    <div className="w-16 h-16 bg-bg-gray rounded overflow-hidden">
                      <img src={order.image} alt={order.PromotionName} className="w-full h-full object-cover" />
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Help Section */}
      {orders.length > 0 && (
        <div className="mt-8 bg-bg-light rounded-lg p-6">
          <h3 className="font-bold text-text-primary mb-3">도움이 필요하신가요?</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex items-start gap-2">
              <div className="text-lg">📞</div>
              <div>
                <div className="font-bold text-text-primary mb-1">고객센터</div>
                <div className="text-text-meta">1588-0000 (평일 09:00-18:00)</div>
              </div>
            </div>
            <div className="flex items-start gap-2">
              <div className="text-lg">📧</div>
              <div>
                <div className="font-bold text-text-primary mb-1">이메일 문의</div>
                <div className="text-text-meta">support@timedeal.com</div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
