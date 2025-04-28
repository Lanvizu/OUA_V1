package OUA.OUA_V1.order.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
        @NotNull(message = "입찰 가격을 입력해주세요.")
        @Positive(message = "입찰 가격은 0보다 커야 합니다.")
        int orderPrice
) {
}
