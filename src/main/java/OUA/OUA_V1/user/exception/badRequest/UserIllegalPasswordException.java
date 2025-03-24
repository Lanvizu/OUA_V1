package OUA.OUA_V1.user.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class UserIllegalPasswordException extends BadRequestException {

    private static final String MESSAGE = "입력하신 비밀번호의 형식이 맞지 않습니다.";

    public UserIllegalPasswordException() {
        super(MESSAGE);
    }
}
