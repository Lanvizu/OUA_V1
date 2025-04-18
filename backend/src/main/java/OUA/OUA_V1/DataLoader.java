package OUA.OUA_V1;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.repository.OrderRepository;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductCategory;
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
    private final OrderRepository orderRepository;

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
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-02-24%20202531.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-03-12%20140443.png"
        );
        Product product = new Product(member, "product", "테스트용 product입니다.", 100000,
                999999, LocalDateTime.now().plusDays(7), ProductCategory.BOOKS.getCategoryId(), dummyImageUrls);

        Product product2 = new Product(member, "product2", "테스트용 product2입니다.", 123456,
                999999, LocalDateTime.now().plusDays(7), ProductCategory.BEAUTY_CARE.getCategoryId(), dummyImageUrls);

        Product product3 = new Product(member2, "product3", "테스트용 product3입니다.", 123456,
                999999, LocalDateTime.now().plusDays(7), ProductCategory.BUYING_REQUESTS.getCategoryId(), dummyImageUrls);

        productRepository.save(product);
        productRepository.save(product2);
        productRepository.save(product3);

        Order order = new Order(member, product3, 222222);
        Order order2 = new Order(member2, product, 222222);
        Order order3 = new Order(member2, product2, 333333);

        orderRepository.save(order);
        orderRepository.save(order2);
        orderRepository.save(order3);
    }
}
