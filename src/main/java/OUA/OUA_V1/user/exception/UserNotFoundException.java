package OUA.OUA_V1.user.exception;

import OUA.OUA_V1.advice.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    private static final String TARGET = "회원";

    public UserNotFoundException() {
        super(TARGET);
    }
}
