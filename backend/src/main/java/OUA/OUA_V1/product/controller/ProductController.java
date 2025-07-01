package OUA.OUA_V1.product.controller;

import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductPreviewResponse;
import OUA.OUA_V1.product.controller.response.ProductResponse;
import OUA.OUA_V1.product.controller.response.SlicedResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.facade.ProductFacade;
import OUA.OUA_V1.product.facade.ProductLockFacade;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;
    private final ProductLockFacade productLockFacade;
    private final ProductService productService;

    @PostMapping("/product")
    public ResponseEntity<Void> registerProduct(
            @LoginMember Long MemberId,
            ProductRegisterRequest productRequest,
            ProductImagesRequest imagesRequest) {
        Long productId = productFacade.registerProduct(MemberId, productRequest, imagesRequest);
        return ResponseEntity.created(URI.create("/product/" + productId)).build();
    }

    @RequireAuthCheck(targetId = "productId", targetDomain = Product.class)
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") Long productId,
            LoginProfile loginProfile) {
        productLockFacade.deleteWithLock(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    public ResponseEntity<SlicedResponse<ProductPreviewResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedDate,
            @RequestParam(defaultValue = "20") int size
    ) {
        Slice<ProductPreviewResponse> products = productService.findByFiltersWithKeySet(keyword, onSale, categoryId, lastCreatedDate, size);
        return ResponseEntity.ok(new SlicedResponse<>(products.getContent(), products.hasNext()));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("productId") Long productId,
            @LoginMember Long memberId) {
        ProductResponse productResponse = productFacade.getProductWithOwnershipCheck(productId, memberId);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/my-products")
    public ResponseEntity<SlicedResponse<ProductPreviewResponse>> getMyProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedDate,
            @RequestParam(defaultValue = "20") int size,
            @LoginMember Long memberId) {
        Slice<ProductPreviewResponse> products = productService.findByMemberIdWithKeySet(memberId, lastCreatedDate, size);
        return ResponseEntity.ok(new SlicedResponse<>(products.getContent(), products.hasNext()));
    }

    // 수동 상품 정보 업데이트
    @PatchMapping("/product/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable("productId") Long productId,
                                              @LoginMember Long memberId) {
        productLockFacade.finalizeAuctionWithLockIfExpired(productId);
        return ResponseEntity.noContent().build();
    }
}
