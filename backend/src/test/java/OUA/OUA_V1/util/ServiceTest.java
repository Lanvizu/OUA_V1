package OUA.OUA_V1.util;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.util.fixture.LocalDateFixture;import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ServiceTest {

    private static final Clock FIXED_TIME = LocalDateFixture.fixedClock();

    protected Member defaultMember;

    @MockBean
    protected JavaMailSender mailSender;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void resetDb() {
        dbCleaner.truncateEveryTable();
//        defaultMember = memberRepository.save(MemberFixture.ADMIN);
    }
}
