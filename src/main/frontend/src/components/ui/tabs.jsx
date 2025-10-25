import React, { useState, useRef, useEffect } from 'react';

export const Tabs = ({ defaultValue, value: controlledValue, onValueChange, className, children }) => {
  const [internalValue, setInternalValue] = useState(defaultValue);
  const value = controlledValue ?? internalValue;

  const handleValueChange = (newValue) => {
    if (controlledValue === undefined) {
      setInternalValue(newValue);
    }
    onValueChange?.(newValue);
  };

  return (
    <div className={`w-full ${className || ''}`} data-value={value}>
      {React.Children.map(children, child =>
        React.isValidElement(child)
          ? React.cloneElement(child, { activeValue: value, onValueChange: handleValueChange })
          : child
      )}
    </div>
  );
};

export const TabsList = ({ className, children, activeValue, onValueChange }) => {
  const listRef = useRef(null);

  const handleKeyDown = (e) => {
    const triggers = Array.from(listRef.current?.querySelectorAll('[role="tab"]') || []);
    const currentIndex = triggers.findIndex(t => t.getAttribute('data-state') === 'active');

    let nextIndex = currentIndex;

    if (e.key === 'ArrowRight' || e.key === 'ArrowDown') {
      e.preventDefault();
      nextIndex = (currentIndex + 1) % triggers.length;
    } else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') {
      e.preventDefault();
      nextIndex = currentIndex - 1 < 0 ? triggers.length - 1 : currentIndex - 1;
    } else if (e.key === 'Home') {
      e.preventDefault();
      nextIndex = 0;
    } else if (e.key === 'End') {
      e.preventDefault();
      nextIndex = triggers.length - 1;
    }

    if (nextIndex !== currentIndex && triggers[nextIndex]) {
      triggers[nextIndex].click();
      triggers[nextIndex].focus();
    }
  };

  return (
    <div
      ref={listRef}
      role="tablist"
      onKeyDown={handleKeyDown}
      className={`inline-flex h-12 items-center justify-start rounded-lg bg-gray-100 p-1 text-gray-500 w-full ${className || ''}`}
    >
      {React.Children.map(children, child =>
        React.isValidElement(child)
          ? React.cloneElement(child, { activeValue, onValueChange })
          : child
      )}
    </div>
  );
};

export const TabsTrigger = ({ value: tabValue, children, activeValue, onValueChange, className }) => {
  const isActive = activeValue === tabValue;

  const handleClick = () => {
    onValueChange?.(tabValue);
  };

  return (
    <button
      role="tab"
      type="button"
      aria-selected={isActive}
      data-state={isActive ? 'active' : 'inactive'}
      onClick={handleClick}
      className={`
        inline-flex items-center justify-center whitespace-nowrap rounded-md px-4 py-2
        text-sm font-medium ring-offset-white transition-all duration-200
        focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2
        disabled:pointer-events-none disabled:opacity-50
        ${isActive
          ? 'bg-white text-blue-600 shadow-md font-bold border-b-4 border-blue-500 scale-105'
          : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900 hover:scale-102'
        }
        ${className || ''}
      `}
    >
      {children}
    </button>
  );
};

export const TabsContent = ({ value: tabValue, children, activeValue, className }) => {
  const isActive = activeValue === tabValue;

  if (!isActive) return null;

  return (
    <div
      role="tabpanel"
      data-state={isActive ? 'active' : 'inactive'}
      className={`mt-2 ring-offset-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 ${className || ''}`}
    >
      {children}
    </div>
  );
};
