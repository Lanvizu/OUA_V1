package OUA.OUA_V1.product.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductPreviewResponse;
import OUA.OUA_V1.product.domain.Product;
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

    public Page<ProductPreviewResponse> getProductsByFilters(String keyword, Boolean onSale,
                                                      Integer categoryId, Pageable pageable) {
        return productRepository.findAllByFilters(keyword, onSale, categoryId, pageable)
                .map(this::toProductPreviewResponse);
    }

    public Page<ProductPreviewResponse> getProductsByMemberId(Long memberId, Pageable pageable) {
        return productRepository.findAllByMemberId(memberId, pageable)
                .map(this::toProductPreviewResponse);
    }

    private ProductPreviewResponse toProductPreviewResponse(Product product) {
        return new ProductPreviewResponse(
                product.getId(),
                product.getName(),
                product.getHighestOrderPrice(),
                product.getBuyNowPrice(),
                product.getEndDate(),
                product.getImageUrls(),
                product.getStatus()
        );
    }
}
