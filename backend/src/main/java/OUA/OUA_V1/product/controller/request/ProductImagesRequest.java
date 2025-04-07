package OUA.OUA_V1.product.controller.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductImagesRequest(

        List<MultipartFile> images
) {
}
