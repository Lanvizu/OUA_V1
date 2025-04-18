package OUA.OUA_V1.order.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class OrderAlreadyExistsException extends BadRequestException {
    private static final String MESSAGE = "이미 입찰을 등록한 상품에는 입찰할 수 없습니다.";

    public OrderAlreadyExistsException() {
        super(MESSAGE);
    }
}
