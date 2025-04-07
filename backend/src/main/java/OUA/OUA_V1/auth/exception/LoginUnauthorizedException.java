package OUA.OUA_V1.auth.exception;

import OUA.OUA_V1.advice.UnauthorizedException;

public class LoginUnauthorizedException extends UnauthorizedException {

    private static final String MESSAGE = "로그인 정보가 유효하지 않습니다.";

    public LoginUnauthorizedException() {
        super(MESSAGE);
    }
}
