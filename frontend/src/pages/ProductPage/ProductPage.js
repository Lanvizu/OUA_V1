import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import './ProductPage.css';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';
import RightArrowIcon from '../../assets/images/icon-right.png';
import LeftArrowIcon from '../../assets/images/icon-left.png';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const ProductPage = () => {
  const { productId } = useParams();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [product, setProduct] = useState(null);
  
  const [error, setError] = useState('');
  const [ordersError, setOrdersError] = useState('');
  const [cancelError, setCancelError] = useState('');
  const [ordersModalError, setOrdersModalError] = useState('');

  const [orderPrice, setOrderPrice] = useState('');
  const [orderCount, setOrderCount] = useState(0);
  const [showOrdersModal, setShowOrdersModal] = useState(false);
  const [myOrder, setMyOrder] = useState(null);
  const [cancelLoading, setCancelLoading] = useState(false);

  const [ordersModalData, setOrdersModalData] = useState({ content: [], page: {} });
  const [ordersModalPage, setOrdersModalPage] = useState(0);

  const [globalLoading, setGlobalLoading] = useState(false);
  const [loadingMessage, setLoadingMessage] = useState('');

  const [timeLeft, setTimeLeft] = useState({ days: 0, hours: 0, minutes: 0, seconds: 0 });

  const calculateTimeLeft = useCallback(() => {
    if (!product?.endDate) return { days: 0, hours: 0, minutes: 0, seconds: 0 };

    const difference = new Date(product.endDate) - new Date();
    if (difference <= 0) {
      return { days: 0, hours: 0, minutes: 0, seconds: 0 };
    }

    return {
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / 1000 / 60) % 60),
      seconds: Math.floor((difference / 1000) % 60)
    };
  }, [product?.endDate]);

  const formatTime = (time) => {
    const parts = [];
    
    if (time.days > 0) {
      parts.push(`${time.days}일`);
    }
  
    parts.push(
      `${String(time.hours).padStart(2, '0')}시`,
      `${String(time.minutes).padStart(2, '0')}분`,
      `${String(time.seconds).padStart(2, '0')}초`
    );
  
    return parts.join(' ');
  };

  useEffect(() => {
    if (!product?.status === 'ACTIVE') return;
  
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft());
    }, 1000);
  
    return () => clearInterval(timer);
  }, [calculateTimeLeft, product?.status]);

  const getStatusMessage = (status) => {
    switch(status) {
      case 'ENDED':
        return '경매 종료';
      case 'SOLD':
        return '낙찰 완료';
      case 'CANCELED':
        return '판매 취소';
      case 'DELETED':
        return '삭제된 상품';
      default:
        return '경매 정보 없음';
    }
  }

  const handleOrder = async () => {
    if (product.status !== 'ACTIVE') {
      alert("경매가 종료되어 입찰이 불가능합니다");
      return;
    }
    const priceToSubmit = parseInt(orderPrice || myOrder?.orderPrice);

    if (!priceToSubmit || priceToSubmit <= 0) {
      alert('유효한 입찰가를 입력해주세요.');
      return;
    }

    const actionType = myOrder ? "변경" : "입찰";
    const confirmed = window.confirm(
      `${priceToSubmit.toLocaleString()}원으로 ${actionType}하시겠습니까?`
    );
    if (!confirmed) return;

    try {
      const url = myOrder 
        ? `/v1/product/${productId}/orders/${myOrder.orderId}/update`
        : `/v1/product/${productId}/orders`;
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          orderPrice: priceToSubmit
        }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '입찰에 실패했습니다.');
      }

      alert(`${actionType}이 완료되었습니다!`);
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
      setGlobalLoading(true);
      setLoadingMessage('상품 정보를 불러오는 중입니다...');
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
        setGlobalLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  const fetchOrders = useCallback(async () => {
    setOrdersError('');
    setGlobalLoading(true);
    setLoadingMessage('입찰 정보를 불러오는 중입니다...');
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
      setGlobalLoading(false);
    }
  }, [productId]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders, productId]); 

  useEffect(() => {
    if (myOrder) {
      setOrderPrice(myOrder.orderPrice.toString());
    }
  }, [myOrder]);

  if (error) return <div className="error">{error}</div>;

  const handleCancelOrder = async () => {
    if (!myOrder) return;
    if (!window.confirm("정말 입찰을 취소하시겠습니까?")) return;
    
    setCancelLoading(true);
    setCancelError('');
    setGlobalLoading(true);
    setLoadingMessage('입찰을 취소하는 중입니다...');
    try {
      const response = await fetch(`/v1/product/${productId}/orders/${myOrder.orderId}/cancel`, {
        method: 'POST',
        credentials: 'include',
      });
      
      if (!response.ok) throw new Error('입찰 취소에 실패했습니다.');
      await fetchOrders();
      window.alert('입찰이 취소되었습니다.');
      window.location.reload();
    } catch (err) {
      setCancelError(err.message);
    } finally {
      setCancelLoading(false);
      setGlobalLoading(false);
    }
  };

  const handleBuyNow = async () => {
    if (product.status !== 'ACTIVE') {
      alert("경매가 종료되어 즉시 구매가 불가능합니다");
      return;
    }
    if (!window.confirm(`${product.buyNowPrice.toLocaleString()}원에 즉시 구매하시겠습니까?`)) return;
  
    try {
      const response = await fetch(`/v1/product/${productId}/buy-now`, {
        method: 'POST',
        credentials: 'include',
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '즉시 구매에 실패했습니다.');
      }
  
      alert('즉시 구매가 완료되었습니다!');
      window.location.reload();
    } catch (error) {
      alert(error.message);
      console.error('즉시 구매 오류:', error);
    }
  };

  const fetchOrderDetails = async (page = 0) => {
    setOrdersModalError('');
    setGlobalLoading(true);
    setLoadingMessage('입찰 내역을 불러오는 중입니다...');
    try {
      const response = await fetch(`/v1/product/${productId}/total-orders?page=${page}&size=10`, {
        credentials: 'include',
      });
      if (!response.ok) throw new Error('입찰 내역을 불러오는데 실패했습니다.');
      const data = await response.json();
      setOrdersModalData(data);
      setOrdersModalPage(page);
    } catch (err) {
      setOrdersModalError(err.message);
    } finally {
      setGlobalLoading(false);
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
      <LoadingOverlay show={globalLoading} message={loadingMessage} />
      {globalLoading || !product ? null : (
        <>
          <div className="image-carousel">
          {product.imageUrls.length > 0 ? (
            <>
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
            </>
          ) : (
            <div className="placeholder-image">이미지가 없습니다</div>
          )}
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
                <span className="price-label">{product.status === 'ACTIVE' ? 
                  "최고 입찰가" : 
                  "최종 낙찰가"
                }</span>
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
                <span className="info-label">남은 시간</span>
                {product.status === 'ACTIVE' ? (
                  <div className="time-display">
                    <p className="info-value countdown">
                      {formatTime(timeLeft)}
                    </p>
                    <p className="info-value end-time">
                      ({new Date(product.endDate).toLocaleString()})
                    </p>
                  </div>
                ) : (
                  <span className="info-value">
                    {getStatusMessage(product.status)}
                  </span>
                )}
              </div>
            </div>

            {/* 주문 정보 섹션 */}
            <div className="order-info-section">
              {ordersError ? (
                <div className="error">{ordersError}</div>
              ) : (
                <>
                  {/* 주문 수 및 상세 보기 버튼 */}
                  <div className="order-summary">
                    <span>총 입찰 건수: {orderCount}</span>
                    {product.isOwner && (
                      <button 
                        onClick={handleShowOrdersModal}
                        className="view-details-btn"
                        disabled={orderCount === 0}
                      >
                        {orderCount > 0 ? '[입찰 기록]' : '입찰 없음'}
                      </button>
                    )}
                  </div>

                  {/* 주문 모달 창 */}
                  {showOrdersModal && (
                    <div className="modal-overlay">
                      <div className="modal">
                        <div className="modal-header">
                          <h3>상세 입찰 내역</h3>
                          <button onClick={handleCloseOrdersModal} className="modal-close-btn">×</button>
                        </div>
                        {ordersModalError ? (
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
                        onClick={product.status !== 'ACTIVE' ? undefined : handleCancelOrder}
                        disabled={product.status !== 'ACTIVE' || cancelLoading}
                        className="cancel-button"
                      >
                        {product.status !== 'ACTIVE' ? "취소 불가" : (cancelLoading ? '취소 중...' : '취소')}
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
                <div className="order-section-vertical">
                  <div className="order-row">
                  <div className="order-input-group">
                    <button
                      type="button"
                      className="order-arrow left"
                      onClick={() => {
                        const min = product.highestOrderPrice + 1000;
                        setOrderPrice((prev) => {
                          const next = Math.max(min, (parseInt(prev || min, 10) - 1000));
                          return next.toString();
                        });
                      }}
                      disabled={product.status !== 'ACTIVE' || product.isOwner}
                      aria-label="1000원 감소"
                    >
                      <img src={LeftArrowIcon} alt="1000원 감소" />
                    </button>
                    <input
                      type="number"
                      value={orderPrice}
                      onChange={(e) => setOrderPrice(e.target.value.replace(/\D/g, ''))}
                      placeholder="입찰가"
                      className="order-input"
                      min={product.highestOrderPrice + 1000}
                      max={product.buyNowPrice}
                      disabled={product.status !== 'ACTIVE' || product.isOwner}
                    />
                    <span className="input-suffix">원</span>
                    <button
                      type="button"
                      className="order-arrow right"
                      onClick={() => {
                        const min = product.highestOrderPrice + 1000;
                        const max = product.buyNowPrice;
                        setOrderPrice((prev) => {
                          const next = Math.min(max, (parseInt(prev || min, 10) + 1000));
                          return next.toString();
                        });
                      }}
                      disabled={product.status !== 'ACTIVE' || product.isOwner}
                      aria-label="1000원 증가"
                    >
                      <img src={RightArrowIcon} alt="1000원 증가" />
                    </button>
                  </div>
                    <div className="buy-now-price">
                      <span className="buy-now-value">{product.buyNowPrice.toLocaleString()}</span>
                      <span className="input-suffix">원</span>
                    </div>
                  </div>
                  <div className="order-row">
                    <button 
                      className="order-button"
                      onClick={product.status !== 'ACTIVE' ? undefined : handleOrder}
                      disabled={product.status !== 'ACTIVE' || (!orderPrice && !myOrder)}
                      title={product.status !== 'ACTIVE' ? "경매가 종료되어 입찰이 불가능합니다" : ""}
                    >
                      {product.status !== 'ACTIVE' ? "경매 종료" : (myOrder ? "입찰가 변경" : "입찰하기")}
                    </button>
                    <button 
                      className="buy-now-button"
                      onClick={product.status !== 'ACTIVE' ? undefined : handleBuyNow}
                      disabled={product.status !== 'ACTIVE' || product.isOwner}
                      title={product.status !== 'ACTIVE' ? "경매가 종료되어 즉시 구매가 불가능합니다" : ""}
                    >
                      즉시 구매
                    </button>
                  </div>
                </div>
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
        </>
      )}
    </div>
  );
};

export default ProductPage;
