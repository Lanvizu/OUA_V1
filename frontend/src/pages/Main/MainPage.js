import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import './MainPage.css';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';

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

  // Helper function to get query parameters
  const getQueryParam = (key, defaultValue) => searchParams.get(key) || defaultValue;

  // Fetch data based on query parameters
  const fetchProducts = async () => {
    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams({
        page: getQueryParam('page', 0),
        size: getQueryParam('size', 10),
        ...(searchKeyword && { keyword: searchKeyword }),
        ...(onSaleFilter && { onSale: true }),
        ...(selectedCategoryId && { categoryId: selectedCategoryId }),
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

  // Update query parameters when filters change
  const updateFilterParams = () => {
    const updatedParams = new URLSearchParams({
      page: 0,
      size: getQueryParam('size', 10),
      ...(searchKeyword && { keyword: searchKeyword }),
      ...(onSaleFilter && { onSale: true }),
      ...(selectedCategoryId && { categoryId: selectedCategoryId }),
    });
    setSearchParams(updatedParams);
    fetchProducts(); // Trigger fetch after updating params
  };

  // Fetch products whenever query parameters change
  useEffect(() => {
    fetchProducts();
  }, [searchParams]);

  // 상품 클릭 시 상세 페이지로 이동
  const handleProductClick = (productId) => navigate(`/product/${productId}`);

  // 상품 카드 컴포넌트
  const ProductCard = ({ product }) => (
    <div className="product-card" onClick={() => handleProductClick(product.productId)} style={{ cursor: 'pointer' }}>
      {product.imageUrls.length > 0 ? (
        <img src={product.imageUrls[0]} alt={product.name} className="product-card-image" />
      ) : (
        <div className="placeholder-image">이미지가 없습니다</div>
      )}
      <div className="product-card-info">
        <h2 className="product-name">{product.name}</h2>
        <div className="product-price-box">
          <p className="product-price">{product.initialPrice.toLocaleString()}원</p>
          <p className="product-price">{product.buyNowPrice.toLocaleString()}원</p>
        </div>
        <p className="product-end-date">{new Date(product.endDate).toLocaleString()}</p>
      </div>
    </div>
  );

  // 페이지네이션 컴포넌트
  const Pagination = ({ pageInfo }) => (
    <div className="pagination">
      <button onClick={() => updateFilterParams({ page: pageInfo.number - 1 })} disabled={pageInfo.number === 0}>
        이전
      </button>
      <span>
        {pageInfo.number + 1} / {pageInfo.totalPages}
      </span>
      <button
        onClick={() => updateFilterParams({ page: pageInfo.number + 1 })}
        disabled={pageInfo.number + 1 === pageInfo.totalPages}
      >
        다음
      </button>
    </div>
  );

  return (
    <div className="main-container">
      <header className="main-header">
        <h1>경매 상품 검색</h1>
        <div className="search-filter-container">
          {/* 검색 창 */}
          <input
            type="text"
            placeholder="상품명 검색"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />

          {/* 카테고리 선택 */}
          <select value={selectedCategoryId} onChange={(e) => setSelectedCategoryId(e.target.value)}>
            <option value="">전체 카테고리</option>
            {CATEGORY_OPTIONS.map((category) => (
              <option key={category.value} value={category.value}>
                {category.label}
              </option>
            ))}
          </select>

          {/* 즉시구매 가능 필터 */}
          <label className="toggle-filter">
            <input
              type="checkbox"
              checked={onSaleFilter}
              onChange={(e) => setOnSaleFilter(e.target.checked)}
            />
            구매 가능만 보기
          </label>

          {/* 검색 버튼 */}
          <button onClick={updateFilterParams}>검색</button>
        </div>
      </header>

      <main className="main-content">
        {loading && <p>로딩 중...</p>}
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

      <footer className="main-footer">
        <p>&copy; 2025 My Website. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Main;
