package OUA.OUA_V1.auth.util;

import OUA.OUA_V1.advice.ForbiddenException;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.domain.MemberRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthChecker {

    public static void checkAuthority(SecureResource resource, Member member) {
        if (member.getRole() == MemberRole.ADMIN) {
            return;
        }

        if (!resource.isAuthorizedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
