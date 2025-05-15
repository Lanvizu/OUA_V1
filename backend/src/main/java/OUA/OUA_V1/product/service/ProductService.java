package OUA.OUA_V1.product.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductPreviewResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.exception.ProductNotFoundException;
import OUA.OUA_V1.product.exception.badRequest.ProductEndDateExceededException;
import OUA.OUA_V1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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
                request.buyNowPrice(), request.endDate(), request.categoryId(), imageUrls);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Product product) {
        product.cancel();
        product.markAsDeleted();
    }

    private void validateEndDate(LocalDateTime endDate) {
        if (endDate.isBefore(LocalDateTime.now())) {
            throw new ProductEndDateExceededException();
        }
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    public Slice<ProductPreviewResponse> findByFiltersWithKeySet(
            String keyword, Boolean onSale, Integer categoryId, LocalDateTime lastCreatedDate, int size) {
        return productRepository.findByFiltersWithKeySet(keyword, onSale, categoryId, lastCreatedDate, size)
                .map(this::toProductPreviewResponse);
    }

    public Slice<ProductPreviewResponse> findByMemberIdWithKeySet(
            Long memberId, LocalDateTime lastCreatedDate, int size) {
        return productRepository.findByMemberIdWithKeySet(memberId, lastCreatedDate, size)
                .map(this::toProductPreviewResponse);
    }

    private ProductPreviewResponse toProductPreviewResponse(Product product) {
        return new ProductPreviewResponse(
                product.getId(),
                product.getName(),
                product.getHighestOrderPrice(),
                product.getBuyNowPrice(),
                product.getCreatedDate(),
                product.getEndDate(),
                product.getMainImageUrl(),
                product.getStatus()
        );
    }
}
