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
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101008.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101212.png"
        );

        List<String> dummyImageUrls2 = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101454.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101513.png"
        );
        Product product = new Product(member2, "오데썽", "테스트용 상품입니다.", 100000,
                150000, LocalDateTime.now().plusDays(7), ProductCategory.WOMEN_ACCESSORIES.getCategoryId(), dummyImageUrls);

        Product product2 = new Product(member, "Mx Keys Mini", "테스트용 상품2입니다.", 80000,
                140000, LocalDateTime.now().plusDays(10), ProductCategory.BEAUTY_CARE.getCategoryId(), dummyImageUrls2);


        productRepository.save(product);
        productRepository.save(product2);

        Order order = new Order(member, product, 110000);
        Order order2 = new Order(member2, product2, 90000);

        orderRepository.save(order);
        orderRepository.save(order2);
        product.updateHighestOrder(order);
        product2.updateHighestOrder(order2);
    }
}
