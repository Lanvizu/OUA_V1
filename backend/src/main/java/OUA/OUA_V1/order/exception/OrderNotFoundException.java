package OUA.OUA_V1.order.exception;

import OUA.OUA_V1.advice.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    private static final String TARGET = "주문";

    public OrderNotFoundException() {
        super(TARGET);
    }
}
