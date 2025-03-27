package OUA.OUA_V1.member.domain.repository;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.domain.MemberRole;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.util.RepositoryTest;
import OUA.OUA_V1.util.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("회원 레포지토리 테스트")
public class MemberRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이미 DB에 저장된 ID를 가진 사용자를 저장하면, 해당 ID의 사용자 정보를 작성된 정보로 업데이트한다.")
    @Test
    void sameIdUpdate() {
        //given
        Member member = MemberFixture.DOBBY;
        Member saved = memberRepository.save(member);

        //when
        Member updatedMember = new Member(saved.getId(), "email", "NEW_DOBBY", "NEW_NICKNAME",
                "newPassword214!", "01012341234", MemberRole.ADMIN);

        memberRepository.save(updatedMember);

        //then
        Member findMember = memberRepository.findById(saved.getId()).get();
        assertThat(findMember.getPassword()).isEqualTo("newPassword214!");
        assertThat(findMember.getPhone()).isEqualTo("01012341234");
        assertThat(findMember.getName()).isEqualTo("NEW_DOBBY");
        assertThat(findMember.getNickName()).isEqualTo("NEW_NICKNAME");
    }

    @DisplayName("ID가 없는 유저를 저장하면, 순차적으로 ID를 부여한다.")
    @Test
    void saveNoId() {
        //given
        Member member1 = MemberFixture.createDobby();
        Member member2 = MemberFixture.createRush();

        //when
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        //then
        assertThat(savedMember1.getId() + 1).isEqualTo(savedMember2.getId());
    }

    @DisplayName("같은 email을 가진 member를 저장 시, 에러가 발생한다.")
    @Test
    void save_duplicateEmail() {
        //given
        Member member1 = MemberFixture.createDobby();
        Member member2 = MemberFixture.createDobby();

        //when
        memberRepository.save(member1);

        //then
        assertThat(memberRepository.existsByEmail(member2.getEmail())).isTrue();
        assertThatThrownBy(() -> memberRepository.save(member2)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
