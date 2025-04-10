package OUA.OUA_V1.member.controller;

import OUA.OUA_V1.member.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.member.controller.request.EmailRequest;
import OUA.OUA_V1.member.controller.request.MemberCreateRequest;
import OUA.OUA_V1.member.controller.request.NewPasswordRequest;
import OUA.OUA_V1.member.controller.response.VerificationResponse;
import OUA.OUA_V1.member.facade.MemberFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @PostMapping("/email-verification")
    public ResponseEntity<Void> requestSignUpEmailVerification(@RequestBody @Valid EmailRequest request) {
        memberFacade.requestEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-update-email-verification")
    public ResponseEntity<Void> requestPasswordUpdateEmailVerification(@RequestBody @Valid EmailRequest request) {
        memberFacade.requestPasswordUpdateEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code-verification")
    public ResponseEntity<VerificationResponse> verifyCode(@RequestBody @Valid CodeVerificationRequest request) {
        String token = memberFacade.verifyEmailCode(request);
        return ResponseEntity.ok(new VerificationResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberCreateRequest request,
                                       @CookieValue(name = "authToken", required = true) String token) {
        Long memberId = memberFacade.create(request, token);
        return ResponseEntity.created(URI.create("/v1/members/" + memberId)).build();
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid NewPasswordRequest request,
                                               @CookieValue(name = "authToken", required = true) String token) {
        memberFacade.updatePassword(request, token);
        return ResponseEntity.ok().build();
    }
}
