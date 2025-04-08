import React from 'react';
import './MainPage.css';

const Main = () => {
  return (
    <div className="main-container">
      <header className="main-header">
        <h1>환영합니다!</h1>
        <p>메인 페이지에 오신 것을 환영합니다.</p>
      </header>

      <main className="main-content">
        <p>이곳에서 다양한 기능을 탐색할 수 있습니다.</p>
        <div className="button-group">
          <button className="main-button" onClick={() => alert('프로필 페이지로 이동')}>
            프로필 보기
          </button>
          <button className="main-button" onClick={() => alert('설정 페이지로 이동')}>
            설정
          </button>
          <button className="main-button" onClick={() => alert('로그아웃')}>
            로그아웃
          </button>
        </div>
      </main>

      <footer className="main-footer">
        <p>&copy; 2025 My Website. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Main;
