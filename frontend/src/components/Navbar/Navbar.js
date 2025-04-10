import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();

  const isLoggedIn = document.cookie.includes('token=');

  const handleLogout = () => {
    fetch('/v1/auth/logout', {
      method: 'POST',
      credentials: 'include',
    })
      .then((response) => {
        if (response.ok) {
          alert('로그아웃 되었습니다.');
          navigate('/main');
        } else {
          alert('로그아웃 실패');
        }
      })
      .catch((error) => console.error('로그아웃 중 오류 발생:', error));
  };

  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/main">OUA</Link>
      </div>
      <nav className="navbar-links">
        <Link to="/favorites">관심상품</Link>
        <Link to="/add-product">상품 등록</Link>
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
