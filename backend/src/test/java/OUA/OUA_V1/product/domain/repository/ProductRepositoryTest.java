package OUA.OUA_V1.product.domain.repository;

import OUA.OUA_V1.config.TestQueryDslConfig;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.repository.ProductRepository;
import OUA.OUA_V1.util.fixture.MemberFixture;
import OUA.OUA_V1.util.fixture.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

@DataJpaTest
@DisplayName("상품 레포지토리 테스트")
@Import(TestQueryDslConfig.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        memberRepository.deleteAll();
        memberRepository.flush();

        member = memberRepository.save(MemberFixture.createDobby());
    }

    @Test
    @DisplayName("키워드, 카테고리, 판매중 필터로 상품을 조회할 수 있다.")
    void findByFiltersWithKeySet_success() {
        // given
        Product product1 = productRepository.save(ProductFixture.createProduct1(member, "아이폰 14", 10000, 100000));
        Product product2 = productRepository.save(ProductFixture.createProduct1(member, "아이폰 케이스", 10000, 100000));
        Product product3 = productRepository.save(ProductFixture.createProduct1(member, "갤럭시", 10000, 100000));

        // when
        Slice<Product> result = productRepository.findByFiltersWithKeySet(
                "아이폰", true, null, LocalDateTime.now().plusSeconds(1), 10
        );

        // then
        assertThat(result.getContent()).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("아이폰 14", "아이폰 케이스");
    }

    @Test
    @DisplayName("회원 ID로 해당 사용자의 상품을 keyset 기반으로 조회할 수 있다.")
    void findByMemberIdWithKeySet_success() {
        // given
        Product product1 = productRepository.save(ProductFixture.createProduct1(member, "아이템 1", 10000, 100000));
        Product product2 = productRepository.save(ProductFixture.createProduct1(member, "아이템 2", 10000, 100000));
        Product product3 = productRepository.save(ProductFixture.createProduct1(member, "아이템 3", 10000, 100000));

        // when
        Slice<Product> result = productRepository.findByMemberIdWithKeySet(
                member.getId(), LocalDateTime.now().plusSeconds(1), 10
        );

        // then
        assertThat(result.getContent()).hasSize(3)
                .allMatch(p -> p.getMember().getId().equals(member.getId()));
    }
}
