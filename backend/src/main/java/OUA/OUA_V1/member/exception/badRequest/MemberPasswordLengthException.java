package OUA.OUA_V1.member.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.TextLengthException;

public class MemberPasswordLengthException extends TextLengthException {

    private static final String TEXT = "비밀번호";

    public MemberPasswordLengthException(int minLength, int maxLength, int currentLength) {
        super(TEXT, minLength, maxLength, currentLength);
    }
}
