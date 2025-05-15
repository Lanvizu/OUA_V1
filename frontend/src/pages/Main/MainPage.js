import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './MainPage.css';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';
import searchIcon from '../../assets/images/icon-search.png';
import noImageIcon from '../../assets/images/no-image-icon.png';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const Main = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [lastCreatedDate, setLastCreatedDate] = useState(null);
  const [error, setError] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('');
  const [onSaleFilter, setOnSaleFilter] = useState(false);

  const navigate = useNavigate();
  const observer = useRef();

  const fetchProducts = useCallback(async () => {
    if (loading || !hasNext) return;
  
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({
        size: 10,
        ...(searchKeyword && { keyword: searchKeyword }),
        ...(onSaleFilter && { onSale: true }),
        ...(selectedCategoryId && { categoryId: selectedCategoryId }),
        ...(lastCreatedDate && { lastCreatedDate }),
      });
  
      const response = await fetch(`/v1/products?${params}`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '상품 정보를 불러오지 못했습니다.');
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
  }, [lastCreatedDate, searchKeyword, onSaleFilter, selectedCategoryId, hasNext, loading]);
  
  const handleSearch = () => {
    setProducts([]);
    setLastCreatedDate(null);
    setHasNext(true);
  };

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
  
    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasNext && !loading) {
        fetchProductsRef.current();
      }
    });
  
    if (node) observer.current.observe(node);
  }, [hasNext, loading]);

  const handleProductClick = (productId) => navigate(`/product/${productId}`);

  const ProductCard = ({ product, innerRef }) => (
    <div
      ref={innerRef}
      className="product-card"
      onClick={() => handleProductClick(product.productId)}
      style={{ cursor: 'pointer' }}
    >
      <div className="image-container">
        {product.mainImageUrl ? (
          <img src={product.mainImageUrl} alt={product.name} className="product-card-image" />
        ) : (
          <div className="placeholder-image">이미지가 없습니다</div>
        )}
      </div>
      <div className="product-card-info">
        <h2 className="product-name">{product.name}</h2>
        <div className="product-meta">
          <div className="product-price-box">
            <p className="product-highest-price">{product.highestOrderPrice.toLocaleString()}원</p>
            <p className="product-buy-now-price">{product.buyNowPrice.toLocaleString()}원</p>
          </div>
          <p
            className={`product-status ${product.status !== 'ACTIVE' ? 'product-status-inactive' : ''}`}
          >
            {product.status}
          </p>
        </div>
        <p className="product-end-date">{new Date(product.endDate).toLocaleString()}</p>
      </div>
    </div>
  );

  return (
    <div className="main-container">
      <LoadingOverlay show={loading} message="상품 정보를 불러오는 중입니다..." />
      <header className="main-header">
        <div className="search-filter-container">

          {/* 카테고리 선택 */}
          <select value={selectedCategoryId} onChange={(e) => setSelectedCategoryId(e.target.value)}>
            <option value="">전체 카테고리</option>
            {CATEGORY_OPTIONS.map((category) => (
              <option key={category.value} value={category.value}>
                {category.label}
              </option>
            ))}
          </select>

          {/* 검색창 + 검색 버튼 */}
          <div className="search-input-wrapper">
            <input
              type="text"
              placeholder="상품명 검색"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') handleSearch(); }}
            />
            <button
              className="search-icon-btn"
              type="button"
              onClick={handleSearch}
              aria-label="검색"
            >
              <img src={searchIcon} alt="검색" />
            </button>
          </div>

          {/* 즉시구매 가능 필터 */}
          <label className="toggle-filter">
            <input
              type="checkbox"
              checked={onSaleFilter}
              onChange={(e) => setOnSaleFilter(e.target.checked)}
            />
            구매 가능만 보기
          </label>
        </div>
      </header>

      <main className="main-content">
        {error && <p className="error">{error}</p>}
        {!loading && !error && products.length === 0 && <p>표시할 상품이 없습니다.</p>}

        {/* 상품 목록 */}
        <div className="product-list">
          {products.map((product, index) => {
            const isLast = index === products.length - 1;
            return (
              <ProductCard
                key={product.productId}
                product={product}
                innerRef={isLast ? lastProductRef : null}
              />
            );
          })}
        </div>
      </main>
    </div>
  );
};

export default Main;
