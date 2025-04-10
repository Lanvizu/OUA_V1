import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './pages/Landing/LandingPage';
import SignInPage from './pages/SignIn/SignInPage';
import SignUpPage from './pages/SignUp/SignUpPage';
import MainPage from './pages/Main/MainPage';
import UpdatePasswordPage from './pages/UpdatePassword/UpdatePasswordPage';
import Navbar from './components/Navbar/Navbar';

function App() {
  return (
    <Router>
      <Navbar />
        <Routes>
          {/* 랜딩 페이지 */}
          <Route path="/" element={<LandingPage />} />
          
          {/* 메인 페이지 */}
          <Route path="/main" element={<MainPage />} />
          
          {/* 로그인 페이지 */}
          <Route path="/signin" element={<SignInPage />} />

          {/* 회원가입 페이지 */}
          <Route path="/signup" element={<SignUpPage />} />

          {/* 비밀번호 찾기 페이지 */}
          <Route path="/update-password" element={<UpdatePasswordPage />} />

        </Routes>
    </Router>
  );
}

export default App;
