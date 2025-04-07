package OUA.OUA_V1.product.controller.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ProductRegisterRequest(
        @NotBlank(message = "상품 이름을 입력해주세요.")
        String name,

        @NotBlank(message = "상품 설명을 입력해주세요.")
        String description,

        @NotBlank(message = "경매 시작 가격을 입력해주세요.")
        int initialPrice,

        @NotBlank(message = "즉시 구매가를 입력해주세요.")
        int buyNowPrice,

        @NotBlank(message = "경매 종료 시간을 설정해주세요")
        LocalDateTime endDate,

        @NotBlank(message = "카테고리를 설정해주세요")
        Integer categoryId
) {
}
