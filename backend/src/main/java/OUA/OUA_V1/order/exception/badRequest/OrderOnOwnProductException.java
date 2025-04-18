package OUA.OUA_V1.order.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class OrderOnOwnProductException extends BadRequestException {
    private static final String MESSAGE = "자신이 등록한 상품에는 주문(입찰)할 수 없습니다.";

    public OrderOnOwnProductException() {
        super(MESSAGE);
    }
}
