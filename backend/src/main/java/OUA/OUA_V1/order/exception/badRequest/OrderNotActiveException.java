package OUA.OUA_V1.order.exception.badRequest;

public class OrderNotActiveException extends RuntimeException {
    private static final String MESSAGE = "해당 주문은 현재 입찰 상태가 아닙니다.";

    public OrderNotActiveException() {
      super(MESSAGE);
    }
}
