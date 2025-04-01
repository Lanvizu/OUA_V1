package OUA.OUA_V1.product.controller;

import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
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
            @RequestParam(name = "memberId") Long memberId,
            ProductRegisterRequest productRequest, ProductImagesRequest imagesRequest) {
        Long productId = productFacade.registerProduct(memberId, productRequest, imagesRequest);
        return ResponseEntity.created(URI.create("/v1/product/" + productId)).build();
    }

    @PostMapping("/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productFacade.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
