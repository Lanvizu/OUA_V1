import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './ProductPage.css';
import { CATEGORY_OPTIONS } from '../../contants/productCategoties';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const ProductPage = () => {
  const { productId } = useParams();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showOptions, setShowOptions] = useState(false);

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

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="product-container">
      {/* 이미지 캐러셀 섹션 */}
      <div className="image-carousel">
        <button
          className="nav-button prev"
          onClick={handlePrev}
          disabled={product.imageUrls.length <= 1}
        >
          &lt;
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
          &gt;
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
              &lt;
            </button>
            <button className="modal-nav next" onClick={handleNext}>
              &gt;
            </button>
          </div>
        </div>
      )}

      {/* 상품 정보 섹션 */}
      <div className="product-details">
      <div className="product-header">
        <h1 className="product-title">{product.name}</h1>
        <div className="options-toggle">
          <button
            className="toggle-button"
            onClick={() => setShowOptions((prev) => !prev)}
          >
            ⋮ 
          </button>

          {showOptions && (
            <div className="options-menu">
              {product.isOwner && (
                <>
                  <button
                    className="delete-button"
                    onClick={handleDeleteProduct}
                    style={{ backgroundColor: 'red', color: 'white', marginTop: '10px' }}
                  >
                    상품 삭제하기
                  </button>
                </>
              )}
              <button className="option-button">옵션1</button>
              <button className="option-button">옵션2</button> 
              {/* 추후 구현 예정 */}
            </div>
          )}
        </div>
      </div>
        <p className="product-description">{product.description}</p>
        <div className="price-section">
          <div className="price-item">
            <span className="price-label">경매 시작가</span>
            <span className="price-value">{product.initialPrice.toLocaleString()}원</span>
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
            <span className="info-label">경매 종료 시간</span>
            <span className="info-value">{new Date(product.endDate).toLocaleString()}</span>
          </div>
          <div className="info-item">
            <span className="info-label">카테고리</span>
            <span className="info-value category">
              {CATEGORY_OPTIONS.find((opt) => opt.value === product.categoryId)?.label ||
                '알 수 없는 카테고리'}
            </span>
          </div>
        </div>
        <div className="button-group">
        <button className="bid-button">입찰하기</button>
        <button className="buy-now-button">즉시 구매</button>
        </div>
      </div>
    </div>
  );
};

export default ProductPage;
