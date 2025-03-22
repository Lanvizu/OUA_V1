package OUA.OUA_V1.user.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CodeVerificationRequest(
        @NotBlank(message = "인증 코드를 입력해주세요")
        String code
        ) {
}
