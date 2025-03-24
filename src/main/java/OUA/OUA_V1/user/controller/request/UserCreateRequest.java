package OUA.OUA_V1.user.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일의 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickName,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @NotBlank(message = "전화번호를 입력해주세요.")
        String phone,

        String token //수정 필요
){
}
