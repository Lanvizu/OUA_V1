package OUA.OUA_V1.auth.util;

import OUA.OUA_V1.member.domain.Member;

public interface SecureResource {
    boolean isAuthorizedBy(Member member);
}
