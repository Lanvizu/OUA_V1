package OUA.OUA_V1.product.facade;

import OUA.OUA_V1.global.service.GcpStorageService;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.exception.badRequest.ProductDeletedException;
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
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final MemberService memberService;
    private final GcpStorageService gcpStorageService;
    private final OrdersService ordersService;

    @Transactional
    public Long registerProduct(Long memberId, ProductRegisterRequest productRequest, ProductImagesRequest imagesRequest) {
        Member member = memberService.findById(memberId);
        List<String> imageUrls = uploadImages(imagesRequest.images());
        Product product = productService.registerProduct(member, productRequest, imageUrls);
        return product.getId();
    }

    @Transactional
    public void delete(Product product) {
        productService.deleteProduct(product);
        ordersService.failAllByProductId(product.getId());
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

    @Transactional(readOnly = true)
    public ProductResponse getProductWithOwnershipCheck(Long productId, Long memberId) {
        Product product = productService.findById(productId);
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }
        boolean isOwner = product.getMember().getId().equals(memberId);
        return toProductResponse(product, isOwner);
    }

    @Transactional
    public void finalizeAuctionInternal(Product product) {
        if (product.getHighestOrderId() != null) {
            Orders orders = ordersService.findById(product.getHighestOrderId());
            ordersService.confirmOrderFailOthers(product.getId(), orders);
            productService.soldOutProduct(product, true);
        } else {
            productService.soldOutProduct(product, false);
        }
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
}