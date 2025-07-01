package OUA.OUA_V1.product.facade;

import OUA.OUA_V1.global.JvmLockTemplate;
import OUA.OUA_V1.global.service.GcpStorageService;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.exception.badRequest.ProductAlreadyDeletedException;
import OUA.OUA_V1.product.exception.badRequest.ProductClosedException;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLockFacade {

    private final ProductFacade productFacade;
    private final ProductService productService;
    private final GcpStorageService gcpStorageService;
    private final JvmLockTemplate lockTemplate;

    public void finalizeExpiredAuctions() {
        List<Product> expiredAuctions = productService.findExpiredActiveProducts(LocalDateTime.now());
        for (Product product : expiredAuctions) {
            lockTemplate.executeWithLock(product.getId(), () -> {
                productFacade.finalizeAuctionInternal(product);
                return null;
            });
        }
    }

    public void finalizeAuctionWithLockIfExpired(Long productId) {
        lockTemplate.executeWithLock(productId, () -> {
            Product product = productService.findById(productId);
            if (!isProductActive(product) || isAuctionExpired(product)) {
                return null;
            }
            productFacade.finalizeAuctionInternal(product);
            return null;
        });
    }

    private boolean isProductActive(Product product) {
        return product.getStatus() == ProductStatus.ACTIVE;
    }

    private boolean isAuctionExpired(Product product) {
        return product.getEndDate().isAfter(LocalDateTime.now());
    }

    public void deleteWithLock(Long productId) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    validateProductIsActive(product);
                    // 트랜잭션
                    productFacade.delete(product);
                    // GCP 삭제는 트랜잭션이 성공적으로 커밋 후 삭제 진행
                    deleteImages(product.getImageUrls());
                    return null;
                }
        );
    }

    private void deleteImages(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            gcpStorageService.deleteImage(imageUrl);
        }
    }

    // 상품 상태 예외처리
    private void validateProductIsActive(Product product) {
        if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
            throw new ProductClosedException();
        }
    }
}
