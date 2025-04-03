package OUA.OUA_V1;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

//    @Value("${dataloader.enable}")
//    private boolean enableDataLoader;

    @Override
    public void run(ApplicationArguments args) {
        runDataLoader();
    }

    private void runDataLoader() {
        Member member = new Member("member@email.com", "member", "memberNickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member2 = new Member("member2@email.com", "member2", "member2Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234

        memberRepository.save(member);
        memberRepository.save(member2);

        List<String> dummyImageUrls = List.of(
                "https://mock-gcp-url.com/image1.jpg",
                "https://mock-gcp-url.com/image2.jpg"
        );
        Product product = new Product(member, "product", "테스트용 product입니다.", 100000,
                999999, LocalDateTime.now().plusDays(7), dummyImageUrls);

        Product product2 = new Product(member, "product2", "테스트용 product2입니다.", 123456,
                999999, LocalDateTime.now().plusDays(7), dummyImageUrls);

        productRepository.save(product);
        productRepository.save(product2);
    }
}
