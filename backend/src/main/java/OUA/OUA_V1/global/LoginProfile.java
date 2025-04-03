package OUA.OUA_V1.global;

import OUA.OUA_V1.member.domain.MemberRole;

public record LoginProfile(
        Long memberId, MemberRole memberRole
) {
}
