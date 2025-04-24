import React from 'react';
import './Footer.css';

const Footer = () => (
  <footer className="footer">
    <div className="footer-inner">
      <span className="footer-copyright">
        &copy; {new Date().getFullYear()} Junho Lee. All rights reserved.
      </span>
      <span className="footer-links">
        <a href="mailto:ghzm888@gmail.com">ghzm888@gmail.com</a>
        <span className="footer-divider">|</span>
        <a href="https://github.com/Lanvizu/OUA_V1" target="_blank" rel="noopener noreferrer">
          GitHub
        </a>
      </span>
    </div>
  </footer>
);

export default Footer;
