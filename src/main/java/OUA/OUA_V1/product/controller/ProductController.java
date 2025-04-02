package OUA.OUA_V1.product.controller;

import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @PostMapping("/register")
    public ResponseEntity<Void> registerProduct(
            @RequestParam("memberId") Long memberId,
            ProductRegisterRequest productRequest,
            ProductImagesRequest imagesRequest) {
        Long productId = productFacade.registerProduct(memberId, productRequest, imagesRequest);
        return ResponseEntity.created(URI.create("/v1/product/" + productId)).build();
    }

    @RequireAuthCheck(targetId = "productId", targetDomain = Product.class)
    @PostMapping("/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") Long productId,
            LoginProfile loginProfile) {
        productFacade.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
