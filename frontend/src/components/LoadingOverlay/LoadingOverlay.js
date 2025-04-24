import React from 'react';
import './LoadingOverlay.css';

const LoadingOverlay = ({ show = false, message = "로딩 중입니다..." }) => {
  if (!show) return null;
  return (
    <div className="loading-overlay" role="status" aria-live="polite">
      <div className="loading-spinner" />
      <div className="loading-message">{message}</div>
    </div>
  );
};

export default LoadingOverlay;