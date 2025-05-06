package OUA.OUA_V1.member.domain.repository;

import OUA.OUA_V1.config.TestQueryDslConfig;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.util.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("사용자 레포지토리 테스트")
@Import(TestQueryDslConfig.class)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        memberRepository.flush();
    }

    @Test
    @DisplayName("저장된 회원의 이메일을 통해 회원 정보를 조회할 수 있다.")
    void findByEmail_success() {
        Member member1 = MemberFixture.createDobby();
        memberRepository.save(member1);

        Optional<Member> result = memberRepository.findByEmail(member1.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(member1.getName());
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
        assertThat(savedMember1.getId()).isNotNull();
        assertThat(savedMember2.getId()).isNotNull();
        assertThat(savedMember1.getId()).isLessThan(savedMember2.getId());
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
