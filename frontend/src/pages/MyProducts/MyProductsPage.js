import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './MyProductsPage.css';

const IMAGE_BASE_URL = 'https://storage.googleapis.com/oua_bucket/';

const MyProductsPage = () => {
  const [products, setProducts] = useState([]);
  const [pageInfo, setPageInfo] = useState({ size: 10, number: 0, totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchProducts = async (pageNumber = 0) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`/v1/my-products?page=${pageNumber}&size=10`);
      if (!response.ok) {
        throw new Error('상품 데이터를 불러오는 데 실패했습니다.');
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

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  }

  useEffect(() => {
    fetchProducts();
  }, []);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pageInfo.totalPages) {
      fetchProducts(newPage);
    }
  };

  return (
    <div className="main-container">
      <header className="main-header">
        <h1>환영합니다!</h1>
        <p>여기에는 검색 창을 만들 예정이다.</p>
      </header>

      <main className="main-content">
        {loading && <p>로딩 중...</p>}
        {error && <p className="error">{error}</p>}
        {!loading && !error && products.length === 0 && <p>표시할 상품이 없습니다.</p>}

        {/* 상품 목록 */}
        <div className="product-list">
          {products.map((product) => (
            <div key={product.productId}
            className="product-card"
            onClick={() => handleProductClick(product.productId)}
            >
              {/* 첫 번째 이미지만 표시 */}
              {product.imageUrls.length > 0 ? (
                <img src={product.imageUrls[0]} alt={product.name} className="product-card-image"/>
              ) : (
                <div className="placeholder-image">이미지가 없습니다</div>
              )}
              <h2 className="product-name">{product.name}</h2>
              <p className="product-price">시작가: {product.initialPrice.toLocaleString()}원</p>
              <p className="product-price">즉시 구매가: {product.buyNowPrice.toLocaleString()}원</p>
              <p className="product-end-date">종료일: {new Date(product.endDate).toLocaleString()}</p>
            </div>
          ))}
        </div>

        {/* 페이지네이션 */}
        <div className="pagination">
          <button onClick={() => handlePageChange(pageInfo.number - 1)} disabled={pageInfo.number === 0}>
            이전
          </button>
          <span>
            {pageInfo.number + 1} / {pageInfo.totalPages}
          </span>
          <button onClick={() => handlePageChange(pageInfo.number + 1)} disabled={pageInfo.number + 1 === pageInfo.totalPages}>
            다음
          </button>
        </div>
      </main>
    </div>
  );
};

export default MyProductsPage;
