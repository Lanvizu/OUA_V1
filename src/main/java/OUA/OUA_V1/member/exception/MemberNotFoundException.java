package OUA.OUA_V1.member.exception;

import OUA.OUA_V1.advice.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    private static final String TARGET = "회원";

    public MemberNotFoundException() {
        super(TARGET);
    }
}
