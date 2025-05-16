import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './PasswordUpdatePage.css';
import IconEyePrivate from '../../assets/images/icon-eye-private.png';
import IconEyePublic from '../../assets/images/icon-eye-public.png';

const PasswordUpdatePage = () => {
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [isEmailSent, setIsEmailSent] = useState(false);
  const [isVerified, setIsVerified] = useState(false);

  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();

    // 이메일 인증 요청
  const handleEmailSubmit = async () => {
    try {
      const response = await fetch('/v1/members/password-update-email-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        alert('인증 이메일이 성공적으로 전송되었습니다!');
        setIsEmailSent(true);
      } else {
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '인증 이메일 전송에 실패했습니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
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
        // 배포 환경
        // document.cookie = `authToken=${data.token}; path=/; secure; samesite=strict`;

        setIsVerified(true);
      } else {
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '잘못된 인증 코드입니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
      console.error('인증 코드 확인 중 오류 발생:', error);
      alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  // 비밀번호 변경 요청
  const handleUpdatePasswordSubmit = async () => {
    try{
      if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
        return;
      }
      const token = document.cookie.split('; ').find((row) => row.startsWith('authToken='))?.split('=')[1];

      if(!token) {
        alert('토큰을 찾을 수 없습니다. 이메일 인증을 다시 진행해주세요.');
        return;
      }
      const response = await fetch('/v1/members/update-password', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ password }),
        credentials: 'include',
      });

      if(response.ok){
        alert('비밀번호가 성공적으로 변경되었습니다!');
        document.cookie = "authToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
        window.location.href = '/main';
      }else{
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '비밀번호 변경에 실패했습니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    }catch (error) {
      console.error('비밀번호 변경 요청 중 오류 발생:', error);
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

  const handleBackClick = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  return (
    <div className="update-password-page">
      <div className='update-password-title-box'>
        <button className="back-button" onClick={handleBackClick}>이전으로</button>
        <span className='update-password-title'>비밀번호 찾기</span>
        <span className="update-password-subtitle">가입하신 이메일 주소를 본인 인증하고 비밀번호를 재설정해 주세요.</span>
      </div>

      {/* 이메일 입력 및 전송 */}
      {!isVerified && (
        <div className='email-input-form2'>
          <div className="form-group">
            <div className="input-with-button">
              <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={isVerified}
              className={"email-input"}
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
          <div className="form-group">
            <div className="input-with-button">
              <input
                type="text"
                id="verification-code"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                placeholder="인증번호 입력"
                required
                className={"verification-code-input"}
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
        </div>
      )}

      {isEmailSent && isVerified && (
        <div className="update-password-form">
          <div className='update-password-form-box'>
          {/* 비밀번호 입력 */}
            <div className="password-input-wrapper">
              <input
              type={showPassword ? 'text' : 'password'}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="PU-text-input"
              placeholder="새 비밀번호"
              />
              <button
              type="button"
              className="PU-password-toggle"
              onClick={() => setShowPassword(!showPassword)}
              >
              <img
                  src={showPassword ? IconEyePublic : IconEyePrivate}
                  alt={showPassword ? '비밀번호 표시' : '비밀번호 숨김'}
                  className="PU-password-icon"
              />
              </button>
            </div>

          {/* 비밀번호 확인 */}
            <div className="password-input-wrapper">
              <input
              type={showConfirmPassword ? 'text' : 'password'}
              id="confirm-password"
              value={confirmPassword}
              onChange={handleConfirmPasswordChange}
              required
              className="PU-text-input"
              placeholder="새 비밀번호 확인"
              />
              <button
              type="button"
              className="PU-password-toggle"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
              <img
                  src={showConfirmPassword ? IconEyePublic : IconEyePrivate}
                  alt={showConfirmPassword ? '비밀번호 표시' : '비밀번호 숨김'}
                  className="PU-password-icon"
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
          <button onClick={handleUpdatePasswordSubmit} className="update-password-button">
          비밀번호 변경
          </button>
        </div>
      )}
    </div>
  );
};

export default PasswordUpdatePage;