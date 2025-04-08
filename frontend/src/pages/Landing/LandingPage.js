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
                <h1>Welcome to Our Website</h1>
                <p>Your journey starts here.</p>
                <button className="cta-button" onClick={handleGetStartedClick}>
                    Get Started
                </button>
            </header>
        </div>
    );
};

export default LandingPage;
