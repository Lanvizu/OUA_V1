import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './pages/Landing/LandingPage';
import SignInPage from './pages/SignIn/SignInPage';
import SignUpPage from './pages/SignUp/SignUpPage';
import MainPage from './pages/Main/MainPage';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* 랜딩 페이지 */}
          <Route path="/" element={<LandingPage />} />
          
          {/* 메인 페이지 */}
          <Route path="/main" element={<MainPage />} />
          
          {/* 로그인 페이지 */}
          <Route path="/signin" element={<SignInPage />} />

          {/* 회원가입 페이지 */}
          <Route path="/signup" element={<SignUpPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
