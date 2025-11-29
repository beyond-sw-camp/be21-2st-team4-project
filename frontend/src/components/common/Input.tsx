import React, { forwardRef } from 'react';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  fullWidth?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, fullWidth = false, className = '', ...props }, ref) => {
    const inputStyles = `
      w-full px-4 py-2 border rounded
      focus:outline-none focus:ring-2 focus:ring-primary-blue
      transition-all duration-200
      ${error ? 'border-sale-red focus:ring-sale-red' : 'border-border-default'}
      disabled:bg-bg-gray disabled:cursor-not-allowed
    `;

    return (
      <div className={`${fullWidth ? 'w-full' : ''}`}>
        {label && (
          <label className="block text-sm font-medium text-text-primary mb-1">
            {label}
          </label>
        )}
        <input
          ref={ref}
          className={`${inputStyles} ${className}`}
          {...props}
        />
        {error && (
          <p className="mt-1 text-sm text-sale-red">{error}</p>
        )}
        {helperText && !error && (
          <p className="mt-1 text-sm text-text-meta">{helperText}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';
