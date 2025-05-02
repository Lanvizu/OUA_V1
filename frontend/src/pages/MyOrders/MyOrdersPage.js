import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './MyOrdersPage.css';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';
import RightArrowIcon from '../../assets/images/icon-right.png';
import LeftArrowIcon from '../../assets/images/icon-left.png';

const MyOrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [pageInfo, setPageInfo] = useState({ size: 10, number: 0, totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const getQueryParam = (key, defaultValue) => searchParams.get(key) || defaultValue;

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
  };

  useEffect(() => {
    fetchOrders();
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
        <div className='order-dates-and-id'>
          <div className="order-id">주문번호: {order.orderId}</div>
          <span className="order-dates">{new Date(order.productEndDate).toLocaleString()}</span>
        </div>
      </div>
    </div>
  );

  const Pagination = ({ pageInfo }) => (
    <div className="pagination">
      <button
        className="pagination-btn"
        onClick={() => updatePage(pageInfo.number - 1)}
        disabled={pageInfo.totalPages === 0 || pageInfo.number === 0}
        aria-label="이전 페이지"
      >
        <img src={LeftArrowIcon} alt="이전" className="pagination-arrow" />
      </button>
      <span className="pagination-info">
        {pageInfo.number + 1} / {pageInfo.totalPages}
      </span>
      <button
        className="pagination-btn"
        onClick={() => updatePage(pageInfo.number + 1)}
        disabled={pageInfo.totalPages === 0 || pageInfo.number + 1 === pageInfo.totalPages}
        aria-label="다음 페이지"
      >
        <img src={RightArrowIcon} alt="다음" className="pagination-arrow" />
      </button>
    </div>
  );

  return (
    <div className="my-orders-container">
      <LoadingOverlay show={loading} message="주문 정보를 불러오는 중입니다..." />
      <header className="my-orders-header">
        <h2>내 입찰 목록</h2>
      </header>

      <main className="my-orders-content">
        {error && <p className="error">{error}</p>}
        {!loading && !error && orders.length === 0 && <p>표시할 주문이 없습니다.</p>}

        <div className="order-list">
          {orders.map((order) => (
            <OrderCard key={order.orderId} order={order} />
          ))}
        </div>

        <Pagination pageInfo={pageInfo} />
      </main>
    </div>
  );
};

export default MyOrdersPage;
