import React from 'react';

export const Button = ({
  variant = 'default',
  size = 'default',
  className = '',
  disabled = false,
  children,
  ...props
}) => {
  const baseClass = 'inline-flex items-center justify-center rounded-md font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50';

  const variantClasses = {
    default: 'bg-blue-600 text-white hover:bg-blue-700 focus-visible:ring-blue-600',
    destructive: 'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-600',
    outline: 'border border-gray-300 bg-white hover:bg-gray-50 focus-visible:ring-gray-600',
    secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300 focus-visible:ring-gray-600',
    ghost: 'hover:bg-gray-100 focus-visible:ring-gray-600',
  };

  const sizeClasses = {
    default: 'h-10 px-4 py-2',
    sm: 'h-9 rounded-md px-3',
    lg: 'h-11 rounded-md px-8',
    icon: 'h-10 w-10',
  };

  return (
    <button
      className={`${baseClass} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      disabled={disabled}
      {...props}
    >
      {children}
    </button>
  );
};
