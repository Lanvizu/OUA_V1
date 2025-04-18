import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './MyOrdersPage.css';

const MyOrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [pageInfo, setPageInfo] = useState({ size: 10, number: 0, totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  // Helper to get query param with default
  const getQueryParam = (key, defaultValue) => searchParams.get(key) || defaultValue;

  // Fetch my orders
  const fetchOrders = async () => {
    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams({
        page: getQueryParam('page', 0),
        size: getQueryParam('size', 10),
      });

      const response = await fetch(`/v1/my-orders?${params}`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '주문 정보를 불러오지 못했습니다.');
      }

      const data = await response.json();
      setOrders(data.content);
      setPageInfo(data.page);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 페이지네이션 변경
  const updatePage = (page) => {
    const updatedParams = new URLSearchParams({
      page,
      size: getQueryParam('size', 10),
    });
    setSearchParams(updatedParams);
    // fetchOrders는 useEffect에서 자동 호출됨
  };

  useEffect(() => {
    fetchOrders();
    // eslint-disable-next-line
  }, [searchParams]);

  // 주문 상세(상품 상세)로 이동
  const handleProductClick = (productId) => navigate(`/product/${productId}`);

  // 주문 카드 컴포넌트
  const OrderCard = ({ order }) => (
    <div className="order-card" onClick={() => handleProductClick(order.productId)} style={{ cursor: 'pointer' }}>
      <div className="order-card-info">
        <h2 className="order-product-name">{order.productName}</h2>
        <div className="order-meta">
          <span className="order-status">{order.status}</span>
          <span className="order-price">{order.orderPrice.toLocaleString()}원</span>
        </div>
        <div className="order-dates">
          <span>종료일: {new Date(order.productEndDate).toLocaleString()}</span>
        </div>
        <div className="order-id">주문번호: {order.orderId}</div>
      </div>
    </div>
  );

  // 페이지네이션 컴포넌트
  const Pagination = ({ pageInfo }) => (
    <div className="pagination">
      <button onClick={() => updatePage(pageInfo.number - 1)} disabled={pageInfo.number === 0}>
        이전
      </button>
      <span>
        {pageInfo.number + 1} / {pageInfo.totalPages}
      </span>
      <button
        onClick={() => updatePage(pageInfo.number + 1)}
        disabled={pageInfo.number + 1 === pageInfo.totalPages}
      >
        다음
      </button>
    </div>
  );

  return (
    <div className="my-orders-container">
      <header className="my-orders-header">
        <h1>내 주문 목록</h1>
      </header>

      <main className="my-orders-content">
        {loading && <p>로딩 중...</p>}
        {error && <p className="error">{error}</p>}
        {!loading && !error && orders.length === 0 && <p>표시할 주문이 없습니다.</p>}

        <div className="order-list">
          {orders.map((order) => (
            <OrderCard key={order.orderId} order={order} />
          ))}
        </div>

        <Pagination pageInfo={pageInfo} />
      </main>

      <footer className="my-orders-footer">
        <p>&copy; 2025 My Website. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default MyOrdersPage;
