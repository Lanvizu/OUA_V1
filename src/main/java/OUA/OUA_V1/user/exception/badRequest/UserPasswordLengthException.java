package OUA.OUA_V1.user.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.TextLengthException;

public class UserPasswordLengthException extends TextLengthException {

    private static final String TEXT = "비밀번호";

    public UserPasswordLengthException(int minLength, int maxLength, int currentLength) {
        super(TEXT, minLength, maxLength, currentLength);
    }
}
