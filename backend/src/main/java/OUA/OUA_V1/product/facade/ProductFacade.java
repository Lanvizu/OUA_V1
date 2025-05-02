package OUA.OUA_V1.product.facade;

import OUA.OUA_V1.global.RedisLockTemplate;
import OUA.OUA_V1.global.service.GcpStorageService;
import OUA.OUA_V1.global.service.RedisService;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.service.OrderService;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.exception.badRequest.ProductIllegalFileException;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final MemberService memberService;
    private final GcpStorageService gcpStorageService;
    private final RedisLockTemplate lockTemplate;
    private final RedisService redisService;
    private final OrderService orderService;

    @Transactional
    public Long registerProduct(Long memberId, ProductRegisterRequest productRequest, ProductImagesRequest imagesRequest) {
        Member member = memberService.findById(memberId);
        List<String> imageUrls = uploadImages(imagesRequest.images());
        Product product = productService.registerProduct(member, productRequest, imageUrls);
        redisService.setAuctionExpiration(product.getId(), product.getEndDate());
        return product.getId();
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productService.findById(productId);
        List<String> imageUrls = product.getImageUrls();
        deleteImages(imageUrls);
        productService.deleteProduct(product);
    }

    private void deleteImages(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            gcpStorageService.deleteImage(imageUrl);
        }
    }

    private List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(this::uploadSingleImage)
                .filter(Objects::nonNull)
                .toList();
    }

    private String uploadSingleImage(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ProductIllegalFileException();
        }
        return gcpStorageService.uploadImage(file);
    }

    public ProductResponse getProductWithOwnershipCheck(Long productId, Long memberId) {
        Product product = productService.findById(productId);
        boolean isOwner = product.getMember().getId().equals(memberId);
        return toProductResponse(product, isOwner);
    }

    private ProductResponse toProductResponse(Product product, boolean isOwner) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getHighestOrderPrice(),
                product.getBuyNowPrice(),
                product.getEndDate(),
                product.getCategoryId(),
                product.getImageUrls(),
                product.getStatus(),
                isOwner
        );
    }

    @Transactional
    public void finalizeAuction(Long productId) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    if(product.getHighestOrderId() != null){
                        Order order = orderService.findById(product.getHighestOrderId());
                        order.confirmOrder();
                        product.soldAuction();
                    }else {
                        product.unSoldAuction();
                    }
                    // 해당 상품에 있는 모든 입찰 상태 변경 해야할까?
                }
        );
    }
}