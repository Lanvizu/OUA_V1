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
        Member member3 = new Member("member3@email.com", "member3", "member3Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member4 = new Member("member4@email.com", "member4", "member4Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member5 = new Member("member5@email.com", "member5", "member5Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member6 = new Member("member6@email.com", "member6", "member6Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member7 = new Member("member7@email.com", "member7", "member7Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member8 = new Member("member8@email.com", "member8", "member8Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234
        Member member9 = new Member("member9@email.com", "member9", "member9Nickname",
                "$2a$10$rG0JsflKdGcORjGFTURYb.npEgtvClK4.3P.EMr/o3SdekrVFxOvG",
                "01012345678"); // password 원문: qwer1234


        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        memberRepository.save(member8);
        memberRepository.save(member9);

        List<String> dummyImageUrls = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101008.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101212.png"
        );

        List<String> dummyImageUrls2 = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101454.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-23%20101513.png"
        );

        List<String> dummyImageUrls3 = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-30%20204947.png"
        );

        List<String> dummyImageUrls4 = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-30%20205335.png"
        );

        List<String> dummyImageUrls5 = List.of(
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-30%20210024.png",
                "%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-04-30%20210035.png"
        );

        Product product = new Product(member2, "오데썽", "테스트용 상품입니다.", 100000,
                150000, LocalDateTime.now().plusDays(7), ProductCategory.WOMEN_ACCESSORIES.getCategoryId(), dummyImageUrls);

        Product product2 = new Product(member3, "Mx Keys Mini", "테스트용 상품2입니다.", 80000,
                140000, LocalDateTime.now().plusDays(2), ProductCategory.BEAUTY_CARE.getCategoryId(), dummyImageUrls2);

        Product product3 = new Product(member4, "플랜룩스 무선 LED 스탠드 조명", "테스트용 상품3입니다.", 10000,
                18000, LocalDateTime.now().plusDays(3), ProductCategory.HOME_APPLIANCE.getCategoryId(), dummyImageUrls3);

        Product product4 = new Product(member5, "아버 2인용 소파", "테스트용 상품4입니다.", 1500000,
                3000000, LocalDateTime.now().plusDays(10), ProductCategory.FURNITURE_INTERIOR.getCategoryId(), dummyImageUrls4);

        Product product5 = new Product(member6, "IAB Studio Hoodie Brown", "테스트용 상품5입니다.", 250000,
                320000, LocalDateTime.now().plusDays(4), ProductCategory.MEN_FASHION_ACCESSORIES.getCategoryId(), dummyImageUrls5);


        productRepository.save(product);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        productRepository.save(product5);

        Order order = new Order(member3, product, 100000);
        Order order2 = new Order(member, product, 110000);
        Order order3 = new Order(member2, product2, 90000);
        Order order4 = new Order(member6, product2, 100000);
        Order order5 = new Order(member, product3, 11000);
        Order order6 = new Order(member2, product3, 12000);
        Order order7 = new Order(member8, product4, 1510000);
        Order order8 = new Order(member9, product4, 1600000);
        Order order9 = new Order(member5, product5, 251000);
        Order order10 = new Order(member7, product5, 252000);

        orderRepository.save(order);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);
        orderRepository.save(order5);
        orderRepository.save(order6);
        orderRepository.save(order7);
        orderRepository.save(order8);
        orderRepository.save(order9);
        orderRepository.save(order10);

        product.updateHighestOrder(order2.getId(), order2.getOrderPrice());
        product2.updateHighestOrder(order4.getId(), order4.getOrderPrice());
        product3.updateHighestOrder(order6.getId(), order6.getOrderPrice());
        product4.updateHighestOrder(order8.getId(), order8.getOrderPrice());
        product5.updateHighestOrder(order10.getId(), order10.getOrderPrice());

    }
}
