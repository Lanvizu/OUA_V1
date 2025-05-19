import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import './MyOrdersPage.css';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';

const MyOrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [lastCreatedDate, setLastCreatedDate] = useState(null);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const observer = useRef();

  const fetchOrders = useCallback(async () => {
    if (loading || !hasNext) return;

    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams({
        size: 10,
        ...(lastCreatedDate && { lastCreatedDate }),
      });

      const response = await fetch(`/v1/my-orders?${params}`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '주문 정보를 불러오지 못했습니다.');
      }

      const data = await response.json();
      const newOrders = data.content;
      setOrders((prev) => [...prev, ...newOrders]);
      setHasNext(data.hasNext);

      if (newOrders.length > 0) {
        const last = newOrders[newOrders.length - 1];
        setLastCreatedDate(last.createdDate);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [lastCreatedDate, hasNext, loading]);

  const fetchOrdersRef = useRef(fetchOrders);
  useEffect(() => {
    fetchOrdersRef.current = fetchOrders;
  }, [fetchOrders]);

  useEffect(() => {
    fetchOrdersRef.current();
  }, []);

  const lastOrderRef = useCallback((node) => {
    if (loading) return;
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasNext && !loading) {
        fetchOrdersRef.current();
      }
    });

    if (node) observer.current.observe(node);
  }, [hasNext, loading]);

  const handleProductClick = (productId) => navigate(`/product/${productId}`);

  const getStatusClass = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'status-active';
      case 'CONFIRMED':
        return 'status-confirmed';
      case 'FAILED':
      case 'CANCELED':
        return 'status-canceled';
      default:
        return 'status-default';
    }
  };

  const OrderCard = ({ order, innerRef }) => (
    <div
      ref={innerRef}
      className="order-card"
      onClick={() => handleProductClick(order.productId)}
      style={{ cursor: 'pointer' }}
    >
      <div className="order-card-info">
        <h2 className="order-product-name">{order.productName}</h2>
        <div className="order-meta">
          <span className={`order-status ${getStatusClass(order.status)}`}>
            {order.status}
          </span>
          <span className="order-price">{order.orderPrice.toLocaleString()}원</span>
        </div>
        <div className="order-dates-and-id">
          <div className="order-id">주문번호: {order.orderId}</div>
          <span className="order-dates">{new Date(order.productEndDate).toLocaleString()}</span>
        </div>
      </div>
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
          {orders.map((order, index) => {
            const isLast = index === orders.length - 1;
            return (
              <OrderCard
                key={order.orderId}
                order={order}
                innerRef={isLast ? lastOrderRef : null}
              />
            );
          })}
        </div>
      </main>
    </div>
  );
};

export default MyOrdersPage;
