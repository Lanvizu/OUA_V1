package OUA.OUA_V1.product.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.exception.ProductNotFoundException;
import OUA.OUA_V1.product.exception.badRequest.ProductEndDateExceededException;
import OUA.OUA_V1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product registerProduct(Member member, ProductRegisterRequest request, List<String> imageUrls) {
        validateEndDate(request.endDate());
        Product product = new Product(member, request.name(), request.description(), request.initialPrice(),
                request.buyNowPrice(), request.endDate(), imageUrls);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    private void validateEndDate(LocalDateTime endDate) {
        if (endDate.isBefore(LocalDateTime.now())) {
            throw new ProductEndDateExceededException();
        }
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::toProductResponse)
                .orElseThrow(ProductNotFoundException::new);
    }

    public Page<ProductResponse> getProductsForSale(Pageable pageable) {
        return productRepository.findAllByStatus(ProductStatus.FOR_SALE, pageable)
                .map(this::toProductResponse);
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getInitialPrice(),
                product.getBuyNowPrice(),
                product.getEndDate(),
                product.getImageUrls()
        );
    }
}
