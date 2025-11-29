import React from 'react';

export interface BadgeProps {
  variant?: 'discount' | 'soldout' | 'limited' | 'new' | 'best' | 'custom';
  color?: string;
  backgroundColor?: string;
  children: React.ReactNode;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  variant = 'discount',
  color,
  backgroundColor,
  children,
  className = '',
}) => {
  const variantStyles = {
    discount: 'bg-sale-red text-white',
    soldout: 'bg-badge-gray text-white',
    limited: 'bg-warning-orange text-white',
    new: 'bg-primary-blue text-white',
    best: 'bg-premium-purple text-white',
    custom: '',
  };

  const customStyles = variant === 'custom' && color && backgroundColor
    ? { color, backgroundColor }
    : {};

  return (
    <span
      className={`inline-block px-2 py-1 text-xs font-bold rounded ${variantStyles[variant]} ${className}`}
      style={customStyles}
    >
      {children}
    </span>
  );
};
