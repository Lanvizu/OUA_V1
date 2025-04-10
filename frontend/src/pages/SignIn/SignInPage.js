import React, { useState } from 'react';
import './SignInPage.css';
import IconEyePrivate from '../../assets/images/icon-eye-private.png';
import IconEyePublic from '../../assets/images/icon-eye-public.png';

const SignInPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleSignInSubmit = async () => {
    if (!email.trim() || !password.trim()) {
      alert('이메일과 비밀번호를 모두 입력해주세요.');
      return;
    }

    try {
      const response = await fetch('/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
        credentials: 'include',
      });

      if (response.ok) {
        alert('로그인에 성공했습니다!');
        window.location.href = '/main';
      } else {
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '로그인에 실패했습니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보:`, errorData);
      }
    } catch (error) {
      console.error('로그인 요청 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  return (
    <div className="signin-page">
      <div className="signin-title-box">
        <span className="signin-title">로그인</span>
        <span className="signin-subtitle">이메일 주소와 비밀번호를 입력해 주세요.</span>
      </div>

      <form className="signin-form" onSubmit={(e) => { e.preventDefault(); handleSignInSubmit(); }}>
        {/* 이메일 입력 */}
        <div className="email-group-inline">
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="text-input"
            placeholder="이메일"
          />
        </div>

        {/* 비밀번호 입력 */}
        <div className="password-input-group">
          <div className="password-input-wrapper">
            <input
              type={showPassword ? 'text' : 'password'}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="text-input"
              placeholder="비밀번호"
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowPassword(!showPassword)}
            >
              <img
                src={showPassword ? IconEyePublic : IconEyePrivate}
                alt={showPassword ? '비밀번호 표시' : '비밀번호 숨김'}
                className="password-icon"
              />
            </button>
          </div>
        </div>
        <div className='signin-button-group'>
          {/* 로그인 버튼 */}
          <button onClick={handleSignInSubmit} className="signin-button">
            로그인
          </button>
        </div>
      </form>  
      <div className="signin-separator"></div>
      <div className="signin-footer">
        <a href="/update-password" className="signin-reset-password-link">비밀번호 찾기</a>
        <div className="signin-footer-separator"></div>
        <a href="/signup" className="signin-register-link">계정 만들기</a>
      </div>
    </div>
  );
};

export default SignInPage;
