import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './MainPage.css';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';
import searchIcon from '../../assets/images/icon-search.png';
import RightArrowIcon from '../../assets/images/icon-right.png';
import LeftArrowIcon from '../../assets/images/icon-left.png';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const Main = () => {
  const [products, setProducts] = useState([]);
  const [pageInfo, setPageInfo] = useState({ size: 10, number: 0, totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('');
  const [onSaleFilter, setOnSaleFilter] = useState(false);

  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const getQueryParam = (key, defaultValue) => searchParams.get(key) || defaultValue;

  const fetchProducts = async () => {
    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams({
        page: getQueryParam('page', 0),
        size: getQueryParam('size', 10),
        ...(searchParams.get('keyword') && { keyword: searchParams.get('keyword') }),
        ...(searchParams.get('onSale') && { onSale: true }),
        ...(searchParams.get('categoryId') && { categoryId: searchParams.get('categoryId') }),
      });

      const response = await fetch(`/v1/products?${params}`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || errorData.title || '상품 정보를 불러오지 못했습니다.');
      }

      const data = await response.json();
      const processedProducts = data.content.map((product) => ({
        ...product,
        imageUrls: product.imageUrls.map((imageId) => `${IMAGE_BASE_URL}${imageId}`),
      }));

      setProducts(processedProducts);
      setPageInfo(data.page);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const updateFilterParams = (params = {}) => {
    const updatedParams = new URLSearchParams({
      page: params.page ?? getQueryParam('page', 0),
      size: params.size ?? getQueryParam('size', 10),
      ...(searchKeyword && { keyword: searchKeyword }),
      ...(onSaleFilter && { onSale: true }),
      ...(selectedCategoryId && { categoryId: selectedCategoryId }),
    });
    setSearchParams(updatedParams);
  };

  useEffect(() => {
    fetchProducts();
  }, [searchParams]);

  const handleProductClick = (productId) => navigate(`/product/${productId}`);

  const ProductCard = ({ product }) => (
    <div className="product-card" onClick={() => handleProductClick(product.productId)} style={{ cursor: 'pointer' }}>
      <div className="image-container">
        {product.imageUrls.length > 0 ? (
          <img src={product.imageUrls[0]} alt={product.name} className="product-card-image" />
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

  // 페이지네이션 컴포넌트
  const Pagination = ({ pageInfo }) => (
    <div className="pagination">
      <button
        className="pagination-btn"
        onClick={() => updateFilterParams({ page: pageInfo.number - 1 })}
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
        onClick={() => updateFilterParams({ page: pageInfo.number + 1 })}
        disabled={pageInfo.totalPages === 0 || pageInfo.number + 1 === pageInfo.totalPages}
        aria-label="다음 페이지"
      >
        <img src={RightArrowIcon} alt="다음" className="pagination-arrow" />
      </button>
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
              onKeyDown={e => { if (e.key === 'Enter') updateFilterParams({ page: 0 }); }}
            />
            <button
              className="search-icon-btn"
              type="button"
              onClick={() => updateFilterParams({ page: 0 })}
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
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>

        {/* 페이지네이션 */}
        <Pagination pageInfo={pageInfo} />
      </main>
    </div>
  );
};

export default Main;
