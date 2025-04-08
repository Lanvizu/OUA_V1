import React, { useState } from 'react';
import './SignUpPage.css';
import IconEyePrivate from '../../assets/images/icon-eye-private.png';
import IconEyePublic from '../../assets/images/icon-eye-public.png';

const SignUpPage = () => {
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [isEmailSent, setIsEmailSent] = useState(false);
  const [isVerified, setIsVerified] = useState(false);

  const [name, setName] = useState('');
  const [nickName, setNickName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [phone, setPhone] = useState('');
  
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // 이메일 인증 요청
  const handleEmailSubmit = async () => {
    try {
      const response = await fetch('/v1/members/email-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        alert('인증 이메일이 성공적으로 전송되었습니다!');
        setIsEmailSent(true);
      } else {
        const errorData = await response.json();
        alert(`오류: ${errorData.title || '인증 이메일 전송에 실패했습니다.'} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
      console.error('이메일 인증 요청 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  // 인증 코드 확인 요청
  const handleCodeSubmit = async () => {
    try {
      const response = await fetch('/v1/members/code-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, code: verificationCode }),
      });

      if (response.ok) {
        const data = await response.json();
        alert(`인증이 완료되었습니다!`);

        document.cookie = `authToken=${data.token}; path=/;`;

        setIsVerified(true);
      } else {
        const errorData = await response.json();
        alert(`오류: ${errorData.title || '잘못된 인증 코드입니다.'} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
      console.error('인증 코드 확인 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  // 회원가입 요청
  const handleSignUpSubmit = async () => {
    try {
      if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
        return;
      }

      const token = document.cookie
        .split('; ')
        .find((row) => row.startsWith('authToken='))
        ?.split('=')[1];

      if (!token) {
        alert('토큰을 찾을 수 없습니다. 이메일 인증을 다시 진행해주세요.');
        return;
      }

      const response = await fetch('/v1/members/signup', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`, 
        },
        body: JSON.stringify({ email, name, nickName, password, phone }),
      });

      if (response.ok) {
        alert('회원가입이 완료되었습니다!');
        window.location.href = '/main';
      } else {
        const errorData = await response.json();
        alert(`오류: ${errorData.title || '회원가입에 실패했습니다.'} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
      console.error('회원가입 요청 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  // 비밀번호 확인 상태 업데이트
  const handleConfirmPasswordChange = (e) => {
    const value = e.target.value;
    setConfirmPassword(value);

    if (value === password) {
      setPasswordMessage('비밀번호가 일치합니다.');
    } else {
      setPasswordMessage('비밀번호가 일치하지 않습니다.');
    }
  };

  return (
    <div className="signup-page">
      <div className='signup-title-box'>
        <span className='signup-title'>회원가입</span>
      </div>

      {/* 이메일 입력 및 전송 */}
      <div className='email-input-form'>
        <div className="form-group-inline">
          <label htmlFor="email" className="form-label">
            이메일 <span className="required">*</span>
          </label>
          <div className="input-with-button">
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={isVerified}
              className={`email-input ${isVerified ? 'input-disabled' : ''}`}
              placeholder="이메일"
            />
            <button
              onClick={handleEmailSubmit}
              className={`submit-button ${isVerified ? 'button-disabled' : ''}`}
              disabled={!email.trim() || isVerified}
            >
              {isVerified ? '인증 완료' : isEmailSent ? '재전송' : '인증 요청'}
            </button>
          </div>
        </div>

        {/* 인증 코드 입력 */}
        {isEmailSent && !isVerified && (
          <div className="form-group-inline">
            <div className="input-with-button">
              <input
                type="text"
                id="verification-code"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                placeholder="인증번호 입력"
                required
                disabled={!isEmailSent}
                className={`verification-code-input ${!isEmailSent ? 'input-disabled' : ''}`}
              />
              <button
                onClick={handleCodeSubmit}
                className="submit-button"
                disabled={!verificationCode.trim()}
              >
                인증하기
              </button>
            </div>
          </div>
        )}
      </div>

      <div className="signup-form">
        {/* 이름 입력 */}
        <div className="form-group-inline">
          <label htmlFor="name" className="form-label">
            이름 <span className="required">*</span>
          </label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            className="text-input"
            placeholder="이름 입력"
          />
        </div>

        {/* 닉네임 입력 */}
        <div className="form-group-inline">
          <label htmlFor="nickName" className="form-label">
            닉네임 <span className="required">*</span>
          </label>
          <input
            type="text"
            id="nickName"
            value={nickName}
            onChange={(e) => setNickName(e.target.value)}
            required
            className="text-input"
            placeholder="닉네임 입력"
          />
        </div>

        {/* 비밀번호 입력 */}
        <div className="form-group-inline password-input-group">
          <label htmlFor="password" className="form-label">
            비밀번호 <span className="required">*</span>
          </label>
          <p className="password-guide">영문, 숫자, 특수문자 포함 8~30자로 입력해 주세요.</p>
          <div className="password-input-wrapper">
            <input
              type={showPassword ? 'text' : 'password'}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="text-input"
              placeholder="비밀번호 입력"
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

        {/* 비밀번호 확인 */}
        <div className="form-group-inline password-input-group">
          <label htmlFor="confirm-password" className="form-label">
            비밀번호 확인 <span className="required">*</span>
          </label>
          <div className="password-input-wrapper">
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              id="confirm-password"
              value={confirmPassword}
              onChange={handleConfirmPasswordChange}
              required
              className="text-input"
              placeholder="비밀번호 확인"
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            >
              <img
                src={showConfirmPassword ? IconEyePublic : IconEyePrivate}
                alt={showConfirmPassword ? '비밀번호 표시' : '비밀번호 숨김'}
                className="password-icon"
              />
            </button>
          </div>
          {passwordMessage && (
            <small
              className={`password-message ${
                password === confirmPassword ? 'match' : 'mismatch'
              }`}
            >
              {passwordMessage}
            </small>
          )}
        </div>

        {/* 전화번호 입력 */}
        <div className="form-group-inline">
          <label htmlFor="phone" className="form-label">
            전화번호 <span className="required">*</span>
          </label>
          <input
            type="text"
            id="phone"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            required
            className="text-input"
            placeholder="'-' 없이 입력하세요."
          />
        </div>

        {/* 회원가입 버튼 */}
        <button onClick={handleSignUpSubmit} className="register-button">
          계정 만들기
        </button>
      </div>
    </div>
  );
};

export default SignUpPage;
