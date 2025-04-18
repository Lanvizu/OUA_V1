import React, { useState } from 'react';
import './ProductRegisterPage.css';
import Select from 'react-select';
import { CATEGORY_OPTIONS } from '../../constants/productCategoties';

const ProductRegisterPage = () => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [initialPrice, setInitialPrice] = useState('');
  const [buyNowPrice, setBuyNowPrice] = useState('');
  const [endDate, setEndDate] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [images, setImages] = useState([]);

  const handleImageChange = (e) => {
    setImages([...e.target.files]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name || !description || !initialPrice || !buyNowPrice || !endDate || !categoryId) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    const formData = new FormData();
    formData.append('name', name);
    formData.append('description', description);
    formData.append('categoryId', categoryId);
    formData.append('initialPrice', initialPrice);
    formData.append('buyNowPrice', buyNowPrice);
    formData.append('endDate', endDate);

    images.forEach((image) => {
      formData.append('images', image);
    });

    try {
      const response = await fetch('/v1/product/register', {
        method: 'POST',
        body: formData,
        credentials: 'include',
      });

      if (response.ok) {
        alert('상품 등록에 성공했습니다!');
        const location = response.headers.get('Location');
        if (location) {
          window.location.href = location;
        } else{
          window.location.href = '/main';
        }
      } else {
        const errorData = await response.json();
        const errorMessage = errorData.detail || errorData.title || '상품 등록에 실패했습니다.';
        alert(`오류: ${errorMessage} (상태 코드: ${errorData.status})`);
        console.error(`오류 세부 정보: ${JSON.stringify(errorData)}`);
      }
    } catch (error) {
      console.error('상품 등록 중 오류 발생:', error);
      alert('상품 등록 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="product-register-container">
      <div className="product-register-title-box">
        <span className="product-register-title">상품 등록</span>
        <span className="product-register-subtitle">상품 정보를 입력해주세요.</span>
      </div>
      <form onSubmit={handleSubmit} className="product-register-form">
        
      <div className="product-register-form-inline">
          <label for="images" className='product-register-form-label'>
            이미지 업로드
          </label>
          <input
            type="file"
            id="images"
            multiple
            accept="image/*"
            onChange={handleImageChange}
          />
        </div>

        <div className="product-register-form-inline">
          <label for="name" className='product-register-form-label'>
            상품 이름 <span className="product-register-required">*</span>
          </label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="text-input"
            placeholder="상품 이름 입력"
          />
        </div>

        <div className="product-register-form-inline">
          <label for="description" className='product-register-form-label'>
            상품 설명 <span className="product-register-required">*</span>
          </label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="text-input"
            placeholder="상품 설명을 입력하세요."
          />
        </div>

        <div className="product-register-form-inline">
          <label for="category" className='product-register-form-label'>
            카테고리 <span className="product-register-required">*</span>
          </label>
          <Select
            options={CATEGORY_OPTIONS}
            onChange={(selectedOption) => setCategoryId(selectedOption.value)}
            placeholder="카테고리 검색..."
            noOptionsMessage={() => "검색 결과 없음"}
            isSearchable
            components={{
              DropdownIndicator: () => (
                <svg
                  width="15"
                  height="15"
                  viewBox="0 0 15 15"
                  fill="none"
                  style={{ marginRight: 8 }}
                >
                  <path d="M6 11L11 6M6 6l5 5" stroke="currentColor"/>
                </svg>
              )
            }}
            styles={{
              control: (base) => ({
                ...base,
                border: '1px solid #ccc',
                borderRadius: '5px',
                padding: '2px 8px'
              })
            }}
          />
        </div>

        <div className="product-register-form-inline">
          <label for="initialPrice" className='product-register-form-label'>
            경매 시작가 <span className="product-register-required">*</span>
          </label>
          <input
            type="number"
            id="initialPrice"
            value={initialPrice}
            onChange={(e) => setInitialPrice(e.target.value)}
            className="text-input"
            placeholder="경매 시작 가격을 입력"
          />
        </div>

        <div className="product-register-form-inline">
          <label for="buyNowPrice" className='product-register-form-label'>
            즉시 구매가 <span className="product-register-required">*</span>
          </label>
          <input
            type="number"
            id="buyNowPrice"
            value={buyNowPrice}
            onChange={(e) => setBuyNowPrice(e.target.value)}
            className="text-input"
            placeholder="즉시 구매가를 입력"
          />
        </div>

        <div className="product-register-form-inline">
          <label for="endDate" className='product-register-form-label'>
            경매 종료 시간 <span className="product-register-required">*</span>
          </label>
          <input
            type="datetime-local"
            id="endDate"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="text-input"
          />
        </div>

        <button type="submit" className="product-register-button">상품 등록</button>
      </form>
    </div>
  );
};

export default ProductRegisterPage;
