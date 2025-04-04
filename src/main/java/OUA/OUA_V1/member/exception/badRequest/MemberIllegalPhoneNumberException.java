package OUA.OUA_V1.member.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class MemberIllegalPhoneNumberException extends BadRequestException {

    private static final String MESSAGE = "올바르지 않은 전화번호 양식입니다.";

    public MemberIllegalPhoneNumberException() {
        super(MESSAGE);
    }
}
