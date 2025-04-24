import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LandingPage from './pages/Landing/LandingPage';
import SignInPage from './pages/SignIn/SignInPage';
import SignUpPage from './pages/SignUp/SignUpPage';
import MainPage from './pages/Main/MainPage';
import PasswordUpdatePage from './pages/PasswordUpdate/PasswordUpdatePage';
import Navbar from './components/Navbar/Navbar';
import ProductRegisterPage from './pages/ProductRegister/ProductRegisterPage';
import ProductPage from './pages/ProductPage/ProductPage';
import MyProductsPage from './pages/MyProducts/MyProductsPage';
import MyOrdersPage from './pages/MyOrders/MyOrdersPage';
import AccountDetailsPage from './pages/AccountDetails/AccountDetailsPage';
import Footer from './components/Footer/Footer';

function App() {
  return (
    <AuthProvider>
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
          <Route path="/password-update" element={<PasswordUpdatePage />} />


          <Route element={<ProtectedRoute />}>
            {/* 내 계정 페이지 */}
            <Route path="/account/details" element={<AccountDetailsPage />} />
            {/* 내 주문 페이지 */}
            <Route path="/my-orders" element={<MyOrdersPage />} />
            {/* 상품 상세 페이지 */}
            <Route path="/product/:productId" element={<ProductPage />} />
            {/* 상품 등록 페이지 */}
            <Route path="/product-register" element={<ProductRegisterPage />} />
            {/* 내 상품 페이지 */}
            <Route path="/my-products" element={<MyProductsPage />} />
          </Route>
        </Routes>
        <Footer />
      </Router>
    </AuthProvider>
    
  );
}

export default App;
