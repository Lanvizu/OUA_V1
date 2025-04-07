package OUA.OUA_V1.auth.exception;

import OUA.OUA_V1.advice.UnauthorizedException;

public class IllegalTokenException extends UnauthorizedException {

    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public IllegalTokenException() {
        super(MESSAGE);
    }
}
