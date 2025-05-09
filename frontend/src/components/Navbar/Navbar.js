import React, { useContext, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const { isLoggedIn, setIsLoggedIn } = useContext(AuthContext);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

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

  const handleSettingsClick = () => {
    if (isLoggedIn) {
      navigate('/account/details');
    } else {
      navigate('/signin');
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/main">OUA</Link>
      </div>

      <button 
        className={`hamburger ${isMenuOpen ? 'active' : ''}`} 
        onClick={toggleMenu}
        aria-label="메뉴 토글"
      >
        <span className="bar"></span>
        <span className="bar"></span>
        <span className="bar"></span>
      </button>

      <nav className={`navbar-links ${isMenuOpen ? 'active' : ''}`}>
        <Link to="/product-register" onClick={closeMenu}>상품 등록</Link>
        <Link to="/my-products" onClick={closeMenu}>내 상품</Link>
        <Link to="/my-orders" onClick={closeMenu}>내 경매</Link>
        
        <button onClick={() => { handleSettingsClick(); closeMenu(); }} className="settings-button">
          설정
        </button>
        
        {isLoggedIn ? (
          <button onClick={() => { handleLogout(); closeMenu(); }} className="logout-button">
            로그아웃
          </button>
        ) : (
          <Link to="/signin" onClick={closeMenu}>로그인</Link>
        )}
      </nav>
    </header>
  );
};

export default Navbar;
