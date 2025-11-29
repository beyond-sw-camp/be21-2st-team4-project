import React from 'react';
import { useCountdown } from '../../hooks/useCountdown';

export interface CountdownTimerProps {
  endTime: string | Date;
  variant?: 'overlay' | 'badge' | 'large';
  className?: string;
}

export const CountdownTimer: React.FC<CountdownTimerProps> = ({
  endTime,
  variant = 'badge',
  className = '',
}) => {
  const timeLeft = useCountdown(endTime);

  const formatTime = () => {
    if (timeLeft.isExpired) {
      return '종료';
    }

    if (timeLeft.days > 0) {
      return `${timeLeft.days}일 ${String(timeLeft.hours).padStart(2, '0')}:${String(timeLeft.minutes).padStart(2, '0')}:${String(timeLeft.seconds).padStart(2, '0')}`;
    }

    return `${String(timeLeft.hours).padStart(2, '0')}:${String(timeLeft.minutes).padStart(2, '0')}:${String(timeLeft.seconds).padStart(2, '0')}`;
  };

  // Critical: less than 1 hour
  const isCritical = timeLeft.total > 0 && timeLeft.total <= 60 * 60 * 1000;

  if (variant === 'overlay') {
    return (
      <div className={`absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 ${className}`}>
        <div className="text-white text-center">
          <div className="text-sm mb-1">남은 시간</div>
          <div className={`text-2xl font-bold font-number ${isCritical ? 'text-sale-red animate-pulse' : ''}`}>
            {formatTime()}
          </div>
        </div>
      </div>
    );
  }

  if (variant === 'large') {
    return (
      <div className={`text-center ${className}`}>
        <div className="text-sm text-text-meta mb-2">남은 시간</div>
        <div className={`text-4xl font-bold font-number ${isCritical ? 'text-sale-red animate-pulse' : 'text-text-primary'}`}>
          {formatTime()}
        </div>
      </div>
    );
  }

  // badge variant
  return (
    <div className={`inline-flex items-center px-2 py-1 bg-black bg-opacity-70 text-white text-sm rounded ${className}`}>
      <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
      </svg>
      <span className={`font-number ${isCritical ? 'text-sale-red font-bold' : ''}`}>
        {formatTime()}
      </span>
    </div>
  );
};
