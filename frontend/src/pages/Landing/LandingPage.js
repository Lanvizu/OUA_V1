import React from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingPage.css';

const LandingPage = () => {
    const navigate = useNavigate();

    const handleGetStartedClick = () => {
        navigate('/main');
    };

    return (
        <div className="landing-page">
            <header className="landing-header">
                <h1>Online Used Auction</h1>
                <p>중고 물품을 경매 방식으로 사고팔 수 있는 개인 프로젝트입니다.</p>
                <ul className="feature-list">
                    <li>회원 가입 및 로그인</li>
                    <li>다양한 카테고리별 중고 상품 등록</li>
                    <li>실시간 경매 참여 및 즉시 구매 기능</li>
                    <li>입찰 내역 및 낙찰 결과 확인</li>
                </ul>
                <button className="cta-button" onClick={handleGetStartedClick}>
                    시작하기
                </button>
            </header>
        </div>
    );
};

export default LandingPage;
