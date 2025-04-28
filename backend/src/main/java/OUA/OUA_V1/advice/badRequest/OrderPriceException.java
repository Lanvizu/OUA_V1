package OUA.OUA_V1.advice.badRequest;

public class OrderPriceException extends BadRequestException {

    private static final String MESSAGE_NOTICE_MIN_MAX = "입찰 가격은 최소 %d원, 최대 %d원 사이여야 합니다. 현재 입력 가격: %d원";

    public OrderPriceException(int minPrice, int maxPrice, int currentPrice) {
        super(String.format(MESSAGE_NOTICE_MIN_MAX, minPrice, maxPrice, currentPrice));
    }
}
