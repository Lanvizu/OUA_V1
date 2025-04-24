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
      <div className='account-detail-title'>계정 정보</div>
      
      <form onSubmit={handleNicknameUpdate} className="details-form">
        <div className="account-detail-form-group">
          <label>이메일</label>
          <div className="email-group-inline">
            {accountDetails.email}
          </div>
        </div>

        <div className="account-detail-form-group">
          <label>이름</label>
          <div className="name-group-inline">
            {accountDetails.name}
          </div>
        </div>

        <div className="account-detail-form-group">
          <label>닉네임</label>
          <div className="nickname-group-inline">
            <input
              className='account-dtatil-input'
              type="text"
              value={newNickName}
              onChange={(e) => setNewNickName(e.target.value)}
              maxLength="15"
            />
            <button type="submit" className="update-button">
              닉네임 변경
            </button>
          </div>
        </div>

        <div className="account-detail-form-group">
          <label>전화번호</label>
          <div className="phone-group-inline">
            {accountDetails.phone}
          </div>
        </div>

        <div className="account-detail-form-group">
          <a href="/password-update" className="account-detail-password-link">비밀번호 변경 페이지</a>
        </div>

        
      </form>

      <button 
        className="delete-account-button"
        onClick={() => setShowDeleteConfirm(true)}
      >
        계정 삭제
      </button>

      {showDeleteConfirm && (
        <div className="delete-confirm-modal">
          <div className="modal-content">
            <h3>정말 계정을 삭제하시겠습니까?</h3>
            <p>삭제된 계정은 복구할 수 없습니다</p>
            <div className="modal-buttons">
              <button onClick={handleAccountDelete}>삭제</button>
              <button onClick={() => setShowDeleteConfirm(false)}>취소</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AccountDetailsPage;
