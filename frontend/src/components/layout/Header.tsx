import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../common';

export interface HeaderProps {
  user?: {
    id: number;
    name: string;
    money: number;  // 백엔드 스펙: balance → money
  } | null;
  onLogout?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ user, onLogout }) => {
  return (
    <header className="sticky top-0 z-40 bg-white border-b border-border-default shadow-sm">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="text-2xl font-bold text-sale-red">
              ⏰ Timedeal
            </div>
          </Link>

          {/* Navigation */}
          <nav className="hidden md:flex items-center space-x-6">
            <Link
              to="/promotions"
              className="text-text-secondary hover:text-sale-red transition-colors font-medium"
            >
              타임딜
            </Link>
            {user && (
              <>
                <Link
                  to="/orders"
                  className="text-text-secondary hover:text-sale-red transition-colors font-medium"
                >
                  주문내역
                </Link>
                <Link
                  to="/me"
                  className="text-text-secondary hover:text-sale-red transition-colors font-medium"
                >
                  마이페이지
                </Link>
              </>
            )}
          </nav>

          {/* User Section */}
          <div className="flex items-center space-x-4">
            {user ? (
              <>
                <div className="hidden md:flex items-center space-x-2">
                  <span className="text-sm text-text-secondary">
                    {user.name}님
                  </span>
                  <span className="text-sm font-bold text-primary-blue">
                    {user.money.toLocaleString()}원
                  </span>
                </div>
                <Button size="sm" variant="outline" onClick={onLogout}>
                  로그아웃
                </Button>
              </>
            ) : (
              <>
                <Link to="/login">
                  <Button size="sm" variant="ghost">
                    로그인
                  </Button>
                </Link>
                <Link to="/signup">
                  <Button size="sm" variant="primary">
                    회원가입
                  </Button>
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
