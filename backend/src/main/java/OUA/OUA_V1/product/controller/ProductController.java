package OUA.OUA_V1.product.controller;

import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.controller.response.ProductResponse;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.facade.ProductFacade;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;
    private final ProductService productService;

    @PostMapping("/product/register")
    public ResponseEntity<Void> registerProduct(
            @RequestParam("memberId") Long memberId,
            ProductRegisterRequest productRequest,
            ProductImagesRequest imagesRequest) {
        Long productId = productFacade.registerProduct(memberId, productRequest, imagesRequest);
        return ResponseEntity.created(URI.create("/v1/product/" + productId)).build();
    }

    @RequireAuthCheck(targetId = "productId", targetDomain = Product.class)
    @PostMapping("/product/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") Long productId,
            LoginProfile loginProfile) {
        productFacade.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    public ResponseEntity<PagedModel<ProductResponse>> getProducts(
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductResponse> products = productService.getProducts(pageable);
        return ResponseEntity.ok(new PagedModel<>(products));
    }

    @GetMapping("product/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("productId") Long productId) {
        ProductResponse productResponse = productService.findById(productId);
        return ResponseEntity.ok(productResponse);
    }
}
