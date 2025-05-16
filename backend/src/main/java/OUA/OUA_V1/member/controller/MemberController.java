package OUA.OUA_V1.member.controller;

import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.global.util.CookieManager;
import OUA.OUA_V1.member.controller.request.*;
import OUA.OUA_V1.member.controller.response.AccountDetailsResponse;
import OUA.OUA_V1.member.controller.response.VerificationResponse;
import OUA.OUA_V1.member.facade.MemberFacade;
import OUA.OUA_V1.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;
    private final MemberService memberService;
    private final CookieManager cookieManager;

    @PostMapping("/email-verifications")
    public ResponseEntity<Void> requestSignUpEmailVerification(@RequestBody @Valid EmailRequest request) {
        memberFacade.requestEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/email-verifications")
    public ResponseEntity<Void> requestPasswordUpdateEmailVerification(@RequestBody @Valid EmailRequest request) {
        memberFacade.requestPasswordUpdateEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code-verifications")
    public ResponseEntity<VerificationResponse> verifyCode(@RequestBody @Valid CodeVerificationRequest request) {
        String token = memberFacade.verifyEmailCode(request);
        return ResponseEntity.ok(new VerificationResponse(token));
    }

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberCreateRequest request,
                                       @CookieValue(name = "authToken", required = true) String token) {
        Long memberId = memberFacade.create(request, token);
        return ResponseEntity.created(URI.create("/v1/members/" + memberId)).build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid NewPasswordRequest request,
                                               @CookieValue(name = "authToken", required = true) String token) {
        memberFacade.updatePassword(request, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDetailsResponse> getAccountDetails(
            @LoginMember Long memberId) {
        AccountDetailsResponse accountDetails = memberService.getAccountDetails(memberId);
        return ResponseEntity.ok(accountDetails);
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(
            @RequestBody @Valid NickNameUpdateRequest request,
            @LoginMember Long memberId) {
        memberService.updateNickName(memberId, request.nickName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
            @LoginMember Long memberId
    ) {
        memberService.deleteMember(memberId);
        ResponseCookie cookie = cookieManager.clearTokenCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
