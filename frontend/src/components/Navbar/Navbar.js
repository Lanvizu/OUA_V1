import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const { isLoggedIn, setIsLoggedIn } = useContext(AuthContext);

  const handleLogout = async () => {
    try {
      const response = await fetch('/v1/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        setIsLoggedIn(false);
        navigate('/signin');
      } else {
        console.error('로그아웃 실패');
        alert('로그아웃에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('로그아웃 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/main">OUA</Link>
      </div>
      <nav className="navbar-links">
        <Link to="/product-register">상품 등록</Link>
        <Link to="/my-products">내 상품</Link>
        <Link to="/my-auctions">내 경매</Link>
        {isLoggedIn ? (
          <button onClick={handleLogout} className="logout-button">
            로그아웃
          </button>
        ) : (
          <Link to="/signin">로그인</Link>
        )}
      </nav>
    </header>
  );
};

export default Navbar;
