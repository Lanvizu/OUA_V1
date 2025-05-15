import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import './MyProductsPage.css';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';
import noImageIcon from '../../assets/images/no-image-icon.png';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const MyProductsPage = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [lastCreatedDate, setLastCreatedDate] = useState(null);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const observer = useRef();

  const fetchProducts = useCallback(async () => {
    if (loading || !hasNext) return;

    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({
        size: 10,
        ...(lastCreatedDate && { lastCreatedDate }),
      });

      const response = await fetch(`/v1/my-products?${params}`);
      if (!response.ok) {
        throw new Error('상품 데이터를 불러오는 데 실패했습니다.');
      }

      const data = await response.json();
      const newProducts = data.content.map(product => ({
        ...product,
        mainImageUrl: product.mainImageUrl
          ? `${IMAGE_BASE_URL}${product.mainImageUrl}`
          : noImageIcon, 
      }));
      setProducts((prev) => [...prev, ...newProducts]);
      setHasNext(data.hasNext);

      if (newProducts.length > 0) {
        setLastCreatedDate(newProducts[newProducts.length - 1].createdDate);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [lastCreatedDate, hasNext, loading]);

  const fetchProductsRef = useRef(fetchProducts);
  useEffect(() => {
    fetchProductsRef.current = fetchProducts;
  }, [fetchProducts]);

  useEffect(() => {
    fetchProductsRef.current();
  }, []);

  const lastProductRef = useCallback((node) => {
    if (loading) return;
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasNext && !loading) {
        fetchProductsRef.current();
      }
    });

    if (node) observer.current.observe(node);
  }, [hasNext, loading]);

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  };

  const MyProductCard = ({ product, innerRef, onClick }) => (
    <div
      ref={innerRef}
      className="my-products-card"
      onClick={() => onClick(product.productId)}
      style={{ cursor: 'pointer' }}
    >
      <div className="my-products-image-container">
        {product.mainImageUrl ? (
          <img src={product.mainImageUrl} alt={product.name} className="my-products-card-image" />
        ) : (
          <div className="my-products-placeholder-image">이미지가 없습니다</div>
        )}
      </div>
      <div className="my-products-card-info">
        <h3 className="my-products-name">{product.name}</h3>
        
        <div className="product-meta">
          <div className="my-products-price-box">
            <span className="my-products-highest-price">
              {product.highestOrderPrice.toLocaleString()}원
            </span>
            <span className="my-products-buy-now-price">
              {product.buyNowPrice.toLocaleString()}원
            </span>
          </div>
          <p
              className={`product-status ${product.status !== 'ACTIVE' ? 'product-status-inactive' : ''}`}
            >
            {product.status}
          </p>
        </div>
        <div className="my-products-end-date">
          {new Date(product.endDate).toLocaleString()}
        </div>
      </div>
    </div>
  );

  return (
    <div className="my-products-container">
      <LoadingOverlay show={loading} message="상품 정보를 불러오는 중입니다..." />
      <header className="my-products-header">
        <h2>나의 등록 상품들</h2>
      </header>

      <main className="my-products-main">
        {error && <p className="my-products-error">{error}</p>}
        {!loading && !error && products.length === 0 && (
          <p className="my-products-empty">표시할 상품이 없습니다.</p>
        )}
        {/* 상품 목록 */}
        <div className="my-products-list">
          {products.map((product, index) => {
            const isLast = index === products.length - 1;
            return (
              <MyProductCard
                key={product.productId}
                product={product}
                innerRef={isLast ? lastProductRef : null}
                onClick={handleProductClick}
              />
            );
          })}
        </div>
      </main>
    </div>
  );
};

export default MyProductsPage;
