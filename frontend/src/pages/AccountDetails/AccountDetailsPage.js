import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import './AccountDetailsPage.css';

const AccountDetailsPage = () => {
  const [accountDetails, setAccountDetails] = useState({
    email: '',
    name: '',
    nickName: '',
    phone: ''
  });
  const [newNickName, setNewNickName] = useState('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const { setIsLoggedIn } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    fetchAccountDetails();
  }, []);

  const fetchAccountDetails = async () => {
    try {
      const response = await fetch('/v1/members/details', {
        credentials: 'include'
      });
      
      if (!response.ok) throw new Error('정보 불러오기 실패');
      
      const data = await response.json();
      setAccountDetails(data);
      setNewNickName(data.nickName);
    } catch (error) {
      alert(error.message);
    }
  };

  const handleNicknameUpdate = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('/v1/members/details/nickname', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ nickName: newNickName })
      });

      if (!response.ok) throw new Error('닉네임 변경 실패');
      
      setAccountDetails(prev => ({ ...prev, nickName: newNickName }));
      alert('닉네임이 성공적으로 변경되었습니다');
    } catch (error) {
      alert(error.message);
    }
  };

  const handleAccountDelete = async () => {
    try {
      const response = await fetch('/v1/members', {
        method: 'DELETE',
        credentials: 'include'
      });

      if (!response.ok) throw new Error('계정 삭제 실패');
      
      setIsLoggedIn(false);
      navigate('/signin');
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <div className="account-details-container">
      <h1 className="account-detail-title">계정 관리</h1>
      
      <form onSubmit={handleNicknameUpdate} className="details-form">
        {/* 이메일 필드 */}
        <div className="account-detail-form-group">
          <label className='AD-label'>이메일 주소</label>
          <div className="email-group-inline">
            {accountDetails.email}
          </div>
        </div>

        {/* 이름 필드 */}
        <div className="account-detail-form-group">
          <label className='AD-label'>실명</label>
          <div className="name-group-inline">
            {accountDetails.name}
          </div>
        </div>

        {/* 닉네임 필드 */}
        <div className="account-detail-form-group">
          <label className='AD-label'>닉네임</label>
          <div className="nickname-group-inline">
            <input
              className='account-dtatil-input'
              type="text" 
              value={newNickName}
              onChange={(e) => setNewNickName(e.target.value)}
              placeholder="새 닉네임 입력"
              maxLength="15"
            />
            <button type="submit" className="update-button">
              <span>변경</span>
            </button>
          </div>
        </div>

        {/* 전화번호 필드 */}
        <div className="account-detail-form-group">
          <label className='AD-label'>연락처</label>
          <div className="phone-group-inline">
            {accountDetails.phone || '미등록'}
          </div>
        </div>

        {/* 보안 설정 섹션 */}
        <div className="account-detail-form-group">
          <label className='AD-label'>보안 설정</label>
          <div className="security-settings">
            <a href="/password-update" className="account-detail-password-link">
              🔒 비밀번호 변경
            </a>
          </div>
        </div>

        {/* 계정 삭제 섹션 */}
        <div className="delete-account-section">
          <label className='AD-label'>계정 삭제</label>
          <button 
            type="button"
            className="delete-account-button"
            onClick={() => setShowDeleteConfirm(true)}
          >
            🗑️ 계정 삭제
          </button>
        </div>
      </form>

      {/* 삭제 확인 모달 */}
      {showDeleteConfirm && (
        <div className="delete-confirm-modal">
          <div className="modal-content">
            <h3>정말 계정을 삭제하시겠습니까?</h3>
            <p>⚠️ 모든 계정 정보와 활동 기록이 영구적으로 삭제됩니다</p>
            
            <div className="modal-buttons">
              <button onClick={handleAccountDelete}>예, 삭제합니다</button>
              <button onClick={() => setShowDeleteConfirm(false)}>취소</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AccountDetailsPage;
