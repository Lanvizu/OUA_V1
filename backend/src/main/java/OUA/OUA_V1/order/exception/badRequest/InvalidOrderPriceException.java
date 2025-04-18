package OUA.OUA_V1.order.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class InvalidOrderPriceException extends BadRequestException {
    private static final String MESSAGE = "입찰 가격은 상품의 초기 가격 이상, 즉시 구매 가격 미만이어야 합니다.";

    public InvalidOrderPriceException() {
        super(MESSAGE);
    }
}
