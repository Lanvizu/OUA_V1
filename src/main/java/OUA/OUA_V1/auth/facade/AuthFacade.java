package OUA.OUA_V1.auth.facade;

import OUA.OUA_V1.auth.exception.LoginFailedException;
import OUA.OUA_V1.auth.controller.request.LoginRequest;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;

    public String login(LoginRequest request) {
        Member member = memberService.findByEmail(request.email());
        if (authService.isNotVerifiedPassword(request.password(), member.getPassword())) {
            throw new LoginFailedException();
        }
        return authService.createToken(member);
    }
}
