package OUA.OUA_V1.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NickNameUpdateRequest(
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Size(min = 2, max = 15, message = "닉네임은 2~15자리로 입력해주세요")
        String nickName
) {
}
