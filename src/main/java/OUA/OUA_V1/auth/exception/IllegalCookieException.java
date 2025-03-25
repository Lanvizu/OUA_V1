package OUA.OUA_V1.auth.exception;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class IllegalCookieException extends BadRequestException {

    private static final String MESSAGE = "유효하지 않은 쿠키입니다.";

    public IllegalCookieException() {
        super(MESSAGE);
    }
}
