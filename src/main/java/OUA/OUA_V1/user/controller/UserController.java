package OUA.OUA_V1.user.controller;

import OUA.OUA_V1.user.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.user.controller.request.EmailRequest;
import OUA.OUA_V1.user.controller.request.UserCreateRequest;
import OUA.OUA_V1.user.controller.response.VerificationResponse;
import OUA.OUA_V1.user.facade.UserFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;
    @PostMapping("/email-verification")
    public ResponseEntity<Void> requestEmailVerification(@RequestBody @Valid EmailRequest request) {
        userFacade.requestEmailVerification(request);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/email-resend")
//    public ResponseEntity<Void> resendEmail(@RequestBody @Valid EmailRequest request) {
//        userFacade.resendEmail(request);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/code-verification")
    public ResponseEntity<VerificationResponse> verifyCode(@RequestBody @Valid CodeVerificationRequest request) {
        String token = userFacade.verifyEmailCode(request);
        return ResponseEntity.ok(new VerificationResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserCreateRequest request) {
        Long userId = userFacade.create(request);
        // created를 통해 201 응답을 설정,
        return ResponseEntity.created(URI.create("/v1/users/" + userId)).build();
    }
}
