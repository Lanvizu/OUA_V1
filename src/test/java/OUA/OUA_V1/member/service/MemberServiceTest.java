package OUA.OUA_V1.member.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.exception.badRequest.MemberPasswordLengthException;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.util.ServiceTest;
import OUA.OUA_V1.util.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    @DisplayName("회원을 ID로 조회한다.")
    @Test
    void findById() {
        //given
        Member savedMember = memberRepository.save(MemberFixture.createDobby());
        Member actualMember = memberService.findById(savedMember.getId());

        //when & then
        assertAll(
                () -> assertDoesNotThrow(() -> memberService.findById(savedMember.getId())),
                () -> assertThat(actualMember.getEmail()).isEqualTo(savedMember.getEmail()),
                () -> assertThat(actualMember.getPhone()).isEqualTo(savedMember.getPhone()),
                () -> assertThat(actualMember.getName()).isEqualTo(savedMember.getName()),
                () -> assertThat(actualMember.getNickName()).isEqualTo(savedMember.getNickName())
        );
    }

    @DisplayName("허용되지 않는 비밀번호 길이로 Member 생성 시 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "short!", // 8자 미만인 길이
            "VeryLongPassword12345678901234567890!!!!!" // 32자 초과인 길이
    })
    void createMemberWithLengthPassword(String password) {
        //given
        String email = "new@email.com";
        String name = "new1";
        String nickname = "new1_nickname";
        String phone = "01012341234";

        //when & then
        assertThatThrownBy(() -> memberService.create(email, name, nickname, password, phone))
                .isInstanceOf(MemberPasswordLengthException.class);
    }

    @DisplayName("회원을 email로 조회한다.")
    @Test
    void findByEmail() {
        //given
        Member savedMember = memberRepository.save(MemberFixture.createDobby());
        Member actualMember = memberService.findByEmail(savedMember.getEmail());

        //when & then
        assertAll(
                () -> assertDoesNotThrow(() -> memberService.findByEmail(savedMember.getEmail())),
                () -> assertThat(actualMember.getId()).isEqualTo(savedMember.getId()),
                () -> assertThat(actualMember.getPhone()).isEqualTo(savedMember.getPhone()),
                () -> assertThat(actualMember.getName()).isEqualTo(savedMember.getName()),
                () -> assertThat(actualMember.getNickName()).isEqualTo(savedMember.getNickName())
        );
    }
}
