package OUA.OUA_V1.member.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("유저 서비스 테스트")
public class MemberServiceTest extends ServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사용자를 생성하면 ID를 반환한다.")
    @Test
    void create() {
        //given
        String email = "new@email.com";
        String name = "new1";
        String nickname = "new1_nickname";
        String password = "newPassword214!";
        String phone = "01012341234";

        //when
        Member member = memberService.create(email, name, nickname, password, phone);

        //then
        Optional<Member> actualMember = memberRepository.findById(member.getId());
        assertAll(
                () -> assertThat(actualMember).isPresent(),
                () -> assertThat(actualMember.get().getEmail()).isEqualTo(email),
                () -> assertThat(actualMember.get().getPhone()).isEqualTo(phone)
        );
    }
}
