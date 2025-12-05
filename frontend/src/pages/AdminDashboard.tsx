import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Promotion } from '../types/promotion';
import { promotionService } from '../services/promotionService';
import { Button } from '../components/common';
import { CountdownTimer } from '../components/promotion/CountdownTimer';

export const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedStatus, setSelectedStatus] = useState<'ALL' | 'ACTIVE' | 'INACTIVE' | 'ENDED'>('ALL');

  useEffect(() => {
    loadPromotions();
  }, []);

  const loadPromotions = async () => {
    try {
      setLoading(true);
      const data = await promotionService.getPromotions();
      setPromotions(data);
      setError('');
    } catch (err: any) {
      console.error('프로모션 로드 실패:', err);
      setError('프로모션 목록을 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    navigate('/admin/login');
  };

  const filteredPromotions = selectedStatus === 'ALL'
    ? promotions
    : promotions.filter(p => p.promotionStatus === selectedStatus);

  const stats = {
    total: promotions.length,
    active: promotions.filter(p => p.promotionStatus === 'ACTIVE').length,
    inactive: promotions.filter(p => p.promotionStatus === 'INACTIVE').length,
    ended: promotions.filter(p => p.promotionStatus === 'ENDED').length,
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-bg-gray">
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-warning-orange mx-auto mb-4"></div>
            <p className="text-text-meta">로딩 중...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-bg-gray">
      {/* Header */}
      <header className="bg-white border-b border-border-default">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold text-text-primary">관리자 대시보드</h1>
              <p className="text-sm text-text-meta mt-1">타임딜 프로모션 관리</p>
            </div>
            <div className="flex items-center gap-3">
              <Button variant="outline" size="sm" onClick={() => navigate('/promotions')}>
                사용자 페이지 보기
              </Button>
              <Button variant="ghost" size="sm" onClick={handleLogout}>
                로그아웃
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg p-6 border border-border-default">
            <div className="flex items-center justify-between mb-2">
              <span className="text-text-meta text-sm">전체 프로모션</span>
              <div className="w-10 h-10 bg-primary-blue bg-opacity-10 rounded-lg flex items-center justify-center">
                <span className="text-xl">📊</span>
              </div>
            </div>
            <div className="text-3xl font-bold text-text-primary">{stats.total}</div>
          </div>

          <div className="bg-white rounded-lg p-6 border border-border-default">
            <div className="flex items-center justify-between mb-2">
              <span className="text-text-meta text-sm">진행 중</span>
              <div className="w-10 h-10 bg-success-green bg-opacity-10 rounded-lg flex items-center justify-center">
                <span className="text-xl">✅</span>
              </div>
            </div>
            <div className="text-3xl font-bold text-success-green">{stats.active}</div>
          </div>

          <div className="bg-white rounded-lg p-6 border border-border-default">
            <div className="flex items-center justify-between mb-2">
              <span className="text-text-meta text-sm">대기 중</span>
              <div className="w-10 h-10 bg-warning-orange bg-opacity-10 rounded-lg flex items-center justify-center">
                <span className="text-xl">⏸️</span>
              </div>
            </div>
            <div className="text-3xl font-bold text-warning-orange">{stats.inactive}</div>
          </div>

          <div className="bg-white rounded-lg p-6 border border-border-default">
            <div className="flex items-center justify-between mb-2">
              <span className="text-text-meta text-sm">종료됨</span>
              <div className="w-10 h-10 bg-badge-gray bg-opacity-10 rounded-lg flex items-center justify-center">
                <span className="text-xl">🏁</span>
              </div>
            </div>
            <div className="text-3xl font-bold text-badge-gray">{stats.ended}</div>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex gap-2">
            <button
              onClick={() => setSelectedStatus('ALL')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedStatus === 'ALL'
                  ? 'bg-primary-blue text-white'
                  : 'bg-white border border-border-default text-text-secondary hover:border-primary-blue'
              }`}
            >
              전체 ({stats.total})
            </button>
            <button
              onClick={() => setSelectedStatus('ACTIVE')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedStatus === 'ACTIVE'
                  ? 'bg-success-green text-white'
                  : 'bg-white border border-border-default text-text-secondary hover:border-success-green'
              }`}
            >
              진행 중 ({stats.active})
            </button>
            <button
              onClick={() => setSelectedStatus('INACTIVE')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedStatus === 'INACTIVE'
                  ? 'bg-warning-orange text-white'
                  : 'bg-white border border-border-default text-text-secondary hover:border-warning-orange'
              }`}
            >
              대기 중 ({stats.inactive})
            </button>
            <button
              onClick={() => setSelectedStatus('ENDED')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                selectedStatus === 'ENDED'
                  ? 'bg-badge-gray text-white'
                  : 'bg-white border border-border-default text-text-secondary hover:border-badge-gray'
              }`}
            >
              종료됨 ({stats.ended})
            </button>
          </div>
          <Button variant="primary" onClick={() => alert('프로모션 생성 기능 구현 예정')}>
            + 새 프로모션 만들기
          </Button>
        </div>

        {/* Error */}
        {error && (
          <div className="bg-sale-red bg-opacity-10 border border-sale-red rounded-lg p-4 mb-6">
            <p className="text-sale-red">{error}</p>
          </div>
        )}

        {/* Promotions Table */}
        {filteredPromotions.length === 0 ? (
          <div className="bg-white rounded-lg p-12 text-center border border-border-default">
            <div className="text-6xl mb-4">📦</div>
            <h2 className="text-2xl font-bold text-text-primary mb-2">
              프로모션이 없습니다
            </h2>
            <p className="text-text-secondary mb-6">
              새로운 타임딜 프로모션을 생성해보세요
            </p>
          </div>
        ) : (
          <div className="bg-white rounded-lg border border-border-default overflow-hidden">
            <table className="w-full">
              <thead className="bg-bg-light border-b border-border-default">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">ID</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">상품명</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">할인율</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">재고</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">시작 시간</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">종료 시간</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">상태</th>
                  <th className="px-6 py-4 text-left text-sm font-bold text-text-primary">작업</th>
                </tr>
              </thead>
              <tbody>
                {filteredPromotions.map((promotion) => {
                  const soldQuantity = promotion.soldQuantity || 0;
                  const remainingQuantity = promotion.totalQuantity - soldQuantity;
                  const stockPercentage = (remainingQuantity / promotion.totalQuantity) * 100;

                  return (
                    <tr key={promotion.id} className="border-b border-border-default hover:bg-bg-light transition-colors">
                      <td className="px-6 py-4 text-sm font-mono text-text-primary">#{promotion.id}</td>
                      <td className="px-6 py-4">
                        <div className="font-medium text-text-primary">
                          {promotion.productName || `상품 #${promotion.productId}`}
                        </div>
                        <div className="text-sm text-text-meta">
                          상품 ID: {promotion.productId}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-lg font-bold text-sale-red">{promotion.discountRate}%</span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="space-y-1">
                          <div className="text-sm font-medium text-text-primary">
                            {remainingQuantity} / {promotion.totalQuantity}
                          </div>
                          <div className="w-20 bg-border-default rounded-full h-2 overflow-hidden">
                            <div
                              className={`h-full ${
                                stockPercentage > 20 ? 'bg-success-green' : 'bg-sale-red'
                              }`}
                              style={{ width: `${stockPercentage}%` }}
                            />
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-text-secondary">
                        {new Date(promotion.startTime).toLocaleString('ko-KR', {
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </td>
                      <td className="px-6 py-4 text-sm text-text-secondary">
                        {new Date(promotion.endTime).toLocaleString('ko-KR', {
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </td>
                      <td className="px-6 py-4">
                        {promotion.promotionStatus === 'ACTIVE' && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-bold bg-success-green bg-opacity-10 text-success-green">
                            진행 중
                          </span>
                        )}
                        {promotion.promotionStatus === 'INACTIVE' && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-bold bg-warning-orange bg-opacity-10 text-warning-orange">
                            대기 중
                          </span>
                        )}
                        {promotion.promotionStatus === 'ENDED' && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-bold bg-badge-gray bg-opacity-10 text-badge-gray">
                            종료됨
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex gap-2">
                          <button
                            onClick={() => navigate(`/promotions/${promotion.id}`)}
                            className="text-sm text-primary-blue hover:underline"
                          >
                            보기
                          </button>
                          <button
                            onClick={() => alert('편집 기능 구현 예정')}
                            className="text-sm text-text-meta hover:text-text-primary"
                          >
                            편집
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
