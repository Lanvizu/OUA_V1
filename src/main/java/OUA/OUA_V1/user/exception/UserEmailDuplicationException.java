package OUA.OUA_V1.user.exception;

import OUA.OUA_V1.advice.ConflictException;

public class UserEmailDuplicationException extends ConflictException {

    private static final String MESSAGE = "이미 가입되어있는 Email 입니다.";

    public UserEmailDuplicationException() {
        super(MESSAGE);
    }
}
