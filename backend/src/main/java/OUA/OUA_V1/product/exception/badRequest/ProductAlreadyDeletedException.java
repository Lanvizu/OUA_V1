package OUA.OUA_V1.product.exception.badRequest;

public class ProductAlreadyDeletedException extends RuntimeException {
    private static final String MESSAGE = "이미 삭제된 상품입니다.";

    public ProductAlreadyDeletedException() {
      super(MESSAGE);
    }
}
