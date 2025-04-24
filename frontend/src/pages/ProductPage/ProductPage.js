import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './ProductPage.css';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';
import RightArrowIcon from '../../assets/images/icon-right.png';
import LeftArrowIcon from '../../assets/images/icon-left.png';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const ProductPage = () => {
  const { productId } = useParams();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [orderPrice, setOrderPrice] = useState('');
  const [orderCount, setOrderCount] = useState(0);
  const [showOrdersModal, setShowOrdersModal] = useState(false);
  const [myOrder, setMyOrder] = useState(null);
  const [ordersLoading, setOrdersLoading] = useState(true);
  const [ordersError, setOrdersError] = useState('');
  const [cancelLoading, setCancelLoading] = useState(false);
  const [cancelError, setCancelError] = useState('');

  const [ordersModalLoading, setOrdersModalLoading] = useState(false);
  const [ordersModalError, setOrdersModalError] = useState('');
  const [ordersModalData, setOrdersModalData] = useState({ content: [], page: {} });
  const [ordersModalPage, setOrdersModalPage] = useState(0);

  const handleOrder = async () => {
    if (!orderPrice || isNaN(orderPrice) || parseInt(orderPrice) <= 0) {
      alert('유효한 입찰가를 입력해주세요.');
      return;
    }

    const confirmed = window.confirm(`${parseInt(orderPrice).toLocaleString()}원으로 입찰하시겠습니까?`);
    if (!confirmed) return;

    try {
      const response = await fetch(`/v1/product/${productId}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          orderPrice: parseInt(orderPrice)
        }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '입찰에 실패했습니다.');
      }

      alert('입찰이 완료되었습니다!');
      window.location.reload();
      setOrderPrice('');
    } catch (error) {
      alert(error.message);
      console.error('입찰 오류:', error);
    }
  };

  const handlePrev = () => {
    setCurrentImageIndex((prev) =>
      prev === 0 ? product.imageUrls.length - 1 : prev - 1
    );
  };

  const handleNext = () => {
    setCurrentImageIndex((prev) =>
      prev === product.imageUrls.length - 1 ? 0 : prev + 1
    );
  };

  const openImageModal = (index) => {
    setCurrentImageIndex(index);
    setShowModal(true);
  };

  const handleDeleteProduct = async () => {
    if (!window.confirm("정말 상품을 삭제하시겠습니까?\n삭제된 상품은 복구할 수 없습니다.")) {
      return;
    }
    try {
      const response = await fetch(`/v1/product/${productId}/delete`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      });

      if (response.ok) {
        alert('상품을 삭제하였습니다.');
        window.location.href = '/main'
      } else{
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '상품 삭제에 실패했습니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
        console.error('로그인 요청 중 오류 발생:', error);
        alert('예기치 못한 오류가 발생했습니다.');
    }
  };

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await fetch(`/v1/product/${productId}`);
        if (!response.ok) {
          throw new Error('상품을 불러오는데 실패했습니다.');
        }
        const data = await response.json();
        const processedData = {
          ...data,
          imageUrls: data.imageUrls.map((imageId) => `${IMAGE_BASE_URL}${imageId}`),
        };
        setProduct(processedData);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  const fetchOrders = async () => {
    setOrdersLoading(true);
    setOrdersError('');
    try {
      const response = await fetch(`/v1/product/${productId}/orders`, {
        credentials: 'include',
      });
      if (!response.ok) throw new Error('주문 정보를 불러오는데 실패했습니다.');
      const data = await response.json();
      setOrderCount(data.productOrdersCount);
      setMyOrder(data.myOrder);
    } catch (err) {
      setOrdersError(err.message);
    } finally {
      setOrdersLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [productId]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  const handleCancelOrder = async () => {
    if (!myOrder) return;
    if (!window.confirm("정말 입찰을 취소하시겠습니까?")) return;
    
    setCancelLoading(true);
    setCancelError('');
    try {
      const response = await fetch(`/v1/orders/${myOrder.orderId}/cancel`, {
        method: 'POST',
        credentials: 'include',
      });
      
      if (!response.ok) throw new Error('입찰 취소에 실패했습니다.');
      await fetchOrders();
      window.alert('입찰이 취소되었습니다.');
    } catch (err) {
      setCancelError(err.message);
    } finally {
      setCancelLoading(false);
    }
  };

  const fetchOrderDetails = async (page = 0) => {
    setOrdersModalLoading(true);
    setOrdersModalError('');
    try {
      const response = await fetch(`/v1/product/${productId}/total-orders?page=${page}&size=10`, {
        credentials: 'include',
      });
      if (!response.ok) throw new Error('주문 내역을 불러오는데 실패했습니다.');
      const data = await response.json();
      setOrdersModalData(data);
      setOrdersModalPage(page);
    } catch (err) {
      setOrdersModalError(err.message);
    } finally {
      setOrdersModalLoading(false);
    }
  };

  const handleShowOrdersModal = () => {
    setShowOrdersModal(true);
    fetchOrderDetails(0);
  };

  const handleCloseOrdersModal = () => {
    setShowOrdersModal(false);
    setOrdersModalData({ content: [], page: {} });
    setOrdersModalError('');
  };

  const handleOrdersModalPageChange = (newPage) => {
    fetchOrderDetails(newPage);
  };
  

  return (
    <div className="product-container">
      <div className="image-carousel">
        <button
          className="nav-button prev"
          onClick={handlePrev}
          disabled={product.imageUrls.length <= 1}
        >
          <img src={LeftArrowIcon} alt="이전" className="product-pagination-arrow" />
        </button>

        {product.imageUrls.map((url, index) => (
          <img
            key={index}
            src={url}
            alt={`상품 이미지 ${index + 1}`}
            className={`product-image ${index === currentImageIndex ? 'active' : ''}`}
            onClick={() => openImageModal(index)}
          />
        ))}

        <button
          className="nav-button next"
          onClick={handleNext}
          disabled={product.imageUrls.length <= 1}
        >
          <img src={RightArrowIcon} alt="다음" className="product-pagination-arrow" />
        </button>

        <div className="carousel-dots">
          {product.imageUrls.map((_, index) => (
            <span
              key={index}
              className={`dot ${index === currentImageIndex ? 'active' : ''}`}
              onClick={() => setCurrentImageIndex(index)}
            />
          ))}
        </div>
      </div>

      {/* 이미지 모달 */}
      {showModal && (
        <div className="image-modal" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <span className="close" onClick={() => setShowModal(false)}>
              &times;
            </span>
            <img
              src={product.imageUrls[currentImageIndex]}
              alt={`확대 보기 ${currentImageIndex + 1}`}
              className="modal-image"
            />
            <button className="modal-nav prev" onClick={handlePrev}>
              <img src={LeftArrowIcon} alt="이전" className="product-pagination-arrow" />
            </button>
            <button className="modal-nav next" onClick={handleNext}>
              <img src={RightArrowIcon} alt="다음" className="product-pagination-arrow" />
            </button>
          </div>
        </div>
      )}

      {/* 상품 정보 섹션 */}
      <div className="product-details">
        <div className="product-header">
          <h1 className="product-title">{product.name}</h1>
          
        </div>
        <div className="info-category">
          <span className="info-value category">
            {CATEGORY_OPTIONS.find((opt) => opt.value === product.categoryId)?.label ||
              '알 수 없는 카테고리'}
          </span>
        </div>
        <div className="price-section">
          <div className="price-item">
            <span className="price-label">최고 입찰가</span>
            <span className="price-value">{product.highestOrderPrice.toLocaleString()}원</span>
          </div>
          <div className="price-item">
            <span className="price-label">즉시 구매가</span>
            <span className="price-value buy-now">
              {product.buyNowPrice.toLocaleString()}원
            </span>
          </div>
        </div>

        <div className="info-box">
          <div className="info-item">
            <span className="info-label">종료 시간</span>
            <span className="info-value">{new Date(product.endDate).toLocaleString()}</span>
          </div>
        </div>

        {/* 주문 정보 섹션 */}
        <div className="order-info-section">
          {ordersLoading ? (
            <div>주문 정보를 불러오는 중...</div>
          ) : ordersError ? (
            <div className="error">{ordersError}</div>
          ) : (
            <>
              {/* 주문 수 및 상세 보기 버튼 */}
              <div className="order-summary">
                <span>총 입찰 건수: {orderCount}</span>
                <button 
                  onClick={handleShowOrdersModal}
                  className="view-details-btn"
                  disabled={orderCount === 0}
                >
                  {orderCount > 0 ? '[입찰 기록]' : '입찰 없음'}
                </button>
              </div>

              {/* 주문 모달 창 */}
              {showOrdersModal && (
                <div className="modal-overlay">
                  <div className="modal">
                    <div className="modal-header">
                      <h3>상세 입찰 내역</h3>
                      <button onClick={handleCloseOrdersModal} className="modal-close-btn">×</button>
                    </div>
                    {ordersModalLoading ? (
                      <div>로딩 중...</div>
                    ) : ordersModalError ? (
                      <div className="error">{ordersModalError}</div>
                    ) : (
                      <>
                        <table className="orders-table">
                          <thead>
                            <tr>
                              <th>주문번호</th>
                              <th>입찰가</th>
                            </tr>
                          </thead>
                          <tbody>
                            {ordersModalData.content.length > 0 ? (
                              ordersModalData.content.map((order) => (
                                <tr key={order.orderId}>
                                  <td>{order.orderId}</td>
                                  <td>{order.orderPrice.toLocaleString()}원</td>
                                </tr>
                              ))
                            ) : (
                              <tr>
                                <td colSpan={3}>입찰 내역이 없습니다.</td>
                              </tr>
                            )}
                          </tbody>
                        </table>
                        {/* 페이지네이션 */}
                        <div className="modal-pagination">
                          <button
                            onClick={() => handleOrdersModalPageChange(ordersModalPage - 1)}
                            disabled={ordersModalPage === 0}
                          >
                            이전
                          </button>
                          <span>
                            {ordersModalPage + 1} / {ordersModalData.page.totalPages || 1}
                          </span>
                          <button
                            onClick={() => handleOrdersModalPageChange(ordersModalPage + 1)}
                            disabled={
                              ordersModalData.page.totalPages
                                ? ordersModalPage + 1 >= ordersModalData.page.totalPages
                                : true
                            }
                          >
                            다음
                          </button>
                        </div>
                      </>
                    )}
                  </div>
                </div>
              )}

              {/* 내 주문 정보 표시 */}
              {myOrder && (
                <div className="my-order-box">
                  <span>내 입찰가: {myOrder.orderPrice.toLocaleString()}원</span>
                  <button
                    onClick={handleCancelOrder}
                    disabled={cancelLoading}
                    className="cancel-button"
                  >
                    {cancelLoading ? '취소 중...' : '취소'}
                  </button>
                  {cancelError && (
                    <div style={{ color: 'red', marginTop: 4 }}>{cancelError}</div>
                  )}
                </div>
              )}
            </>
          )}
        </div>
        <div className="button-group">
          {!product.isOwner && (
            <div className="order-section-vertical">
              <div className="order-row">
                <div className="order-input-group">
                  <input
                    type="number"
                    value={orderPrice}
                    onChange={(e) => setOrderPrice(e.target.value.replace(/\D/g, ''))}
                    placeholder="입찰 가격 입력"
                    className="order-input"
                    min={product.highestOrderPrice + 1000}
                    max={product.buyNowPrice}
                    disabled={!!myOrder}
                  />
                  <span className="input-suffix">원</span>
                </div>
                <div className="buy-now-price">
                  <span className="buy-now-value">{product.buyNowPrice.toLocaleString()}</span>
                  <span className="input-suffix">원</span>
                </div>
              </div>
              <div className="order-row">
                <button
                  className="order-button"
                  onClick={handleOrder}
                  disabled={!orderPrice}
                >
                  입찰하기
                </button>
                <button className="buy-now-button">
                  즉시 구매
                </button>
              </div>
            </div>
          )}
        </div>
        
        {product.isOwner && (
          <div className="delete-section">
            <button 
              className="delete-product-button"
              onClick={handleDeleteProduct}
            >
              상품 삭제
            </button>
          </div>
        )}
      </div>
      <p className="product-description">{product.description}</p>
    </div>
  );
};

export default ProductPage;
