package OUA.OUA_V1.advice.badRequest;

public class OrderPriceException extends BadRequestException {

    private static final String MESSAGE_NOTICE_MIN_MAX = "%s은(는) 최소 %d, 최대 %d입니다. 현재 입력 가격: %d";

    public OrderPriceException(int minPrice, int maxPrice, int currentPrice) {
        super(String.format(MESSAGE_NOTICE_MIN_MAX, minPrice, maxPrice, currentPrice));
    }
}
