// src/components/ProtectedRoute.jsx
import { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const ProtectedRoute = () => {
  const { isLoggedIn, isLoading } = useContext(AuthContext);

  // 로딩 중일 때 스켈레톤 UI 또는 null 반환
  if (isLoading) return null;

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  return isLoggedIn ? <Outlet /> : <Navigate to="/signin" replace />;
};

export default ProtectedRoute;
