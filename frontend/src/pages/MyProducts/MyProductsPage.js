import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './MyProductsPage.css';
import LoadingOverlay from '../../components/LoadingOverlay/LoadingOverlay';

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
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pageInfo.totalPages) {
      fetchProducts(newPage);
    }
  };

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
          {products.map((product) => (
            <div
              key={product.productId}
              className="my-products-card"
              onClick={() => handleProductClick(product.productId)}
              style={{ cursor: 'pointer' }}
            >
              <div className="my-products-image-container">
                {product.imageUrls.length > 0 ? (
                  <img
                    src={product.imageUrls[0]}
                    alt={product.name}
                    className="my-products-card-image"
                  />
                ) : (
                  <div className="my-products-placeholder-image">이미지가 없습니다</div>
                )}
              </div>
              <div className="my-products-card-info">
                <h3 className="my-products-name">{product.name}</h3>
                <div className="my-products-price-box">
                  <span className="my-products-highest-price">{product.highestOrderPrice.toLocaleString()}원</span>
                  <span className="my-products-buy-now-price">{product.buyNowPrice.toLocaleString()}원</span>
                </div>
                <div className="my-products-end-date">
                  {new Date(product.endDate).toLocaleString()}
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* 페이지네이션 */}
        <div className="my-products-pagination">
          <button
            className="my-products-pagination-btn"
            onClick={() => handlePageChange(pageInfo.number - 1)}
            disabled={pageInfo.number === 0}
            aria-label="이전 페이지"
          >
            &lt;
          </button>
          <span className="my-products-pagination-info">
            {pageInfo.number + 1} / {pageInfo.totalPages}
          </span>
          <button
            className="my-products-pagination-btn"
            onClick={() => handlePageChange(pageInfo.number + 1)}
            disabled={pageInfo.number + 1 === pageInfo.totalPages}
            aria-label="다음 페이지"
          >
            &gt;
          </button>
        </div>
      </main>
    </div>
  );
};

export default MyProductsPage;
