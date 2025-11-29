import React from 'react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-bg-gray border-t border-border-default mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Company Info */}
          <div>
            <h3 className="text-lg font-bold text-text-primary mb-3">
              Timedeal
            </h3>
            <p className="text-sm text-text-secondary">
              한정된 시간, 특별한 가격
              <br />
              놓치지 마세요!
            </p>
          </div>

          {/* Links */}
          <div>
            <h4 className="text-sm font-bold text-text-primary mb-3">
              서비스
            </h4>
            <ul className="space-y-2 text-sm text-text-secondary">
              <li>
                <Link to="/promotions" className="hover:text-sale-red transition-colors">
                  타임딜 목록
                </Link>
              </li>
              <li>
                <Link to="/me" className="hover:text-sale-red transition-colors">
                  마이페이지
                </Link>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h4 className="text-sm font-bold text-text-primary mb-3">
              고객지원
            </h4>
            <ul className="space-y-2 text-sm text-text-secondary">
              <li>FAQ</li>
              <li>이용약관</li>
              <li>개인정보처리방침</li>
            </ul>
          </div>
        </div>

        {/* Copyright */}
        <div className="mt-8 pt-6 border-t border-border-default text-center text-sm text-text-meta">
          © 2025 Timedeal. Beyond Software Camp 21기 - 2팀
        </div>
      </div>
    </footer>
  );
};
