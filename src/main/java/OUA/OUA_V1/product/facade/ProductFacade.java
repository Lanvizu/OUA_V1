package OUA.OUA_V1.product.facade;

import OUA.OUA_V1.global.service.GcpStorageService;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.product.controller.request.ProductImagesRequest;
import OUA.OUA_V1.product.controller.request.ProductRegisterRequest;
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

    @Transactional
    public Long registerProduct(Long memberId, ProductRegisterRequest productRequest, ProductImagesRequest imagesRequest) {
        Member member = memberService.findById(memberId);
        List<String> imageUrls = uploadImages(imagesRequest.images());
        Product product = productService.registerProduct(member, productRequest, imageUrls);
        return product.getId();
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productService.findProductById(productId);
        List<String> imageUrls = product.getImageUrls();
        productService.deleteProduct(productId);
        deleteImages(imageUrls);
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
}