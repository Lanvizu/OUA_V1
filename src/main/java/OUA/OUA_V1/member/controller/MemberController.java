package OUA.OUA_V1.member.controller;

import OUA.OUA_V1.member.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.member.controller.request.EmailRequest;
import OUA.OUA_V1.member.controller.request.MemberCreateRequest;
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
    public ResponseEntity<Void> requestEmailVerification(@RequestBody @Valid EmailRequest request) {
        memberFacade.requestEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code-verification")
    public ResponseEntity<VerificationResponse> verifyCode(@RequestBody @Valid CodeVerificationRequest request) {
        String token = memberFacade.verifyEmailCode(request);
        return ResponseEntity.ok(new VerificationResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberCreateRequest request) {
        Long memberId = memberFacade.create(request);
        // created를 통해 201 응답을 설정,
        return ResponseEntity.created(URI.create("/v1/members/" + memberId)).build();
    }
}
