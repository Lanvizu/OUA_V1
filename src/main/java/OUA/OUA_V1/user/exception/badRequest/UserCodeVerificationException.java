package OUA.OUA_V1.user.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class UserCodeVerificationException extends BadRequestException {

    private static final String MESSAGE = "인증 코드가 올바르지 않습니다.";

    public UserCodeVerificationException() {
        super(MESSAGE);
    }
}
