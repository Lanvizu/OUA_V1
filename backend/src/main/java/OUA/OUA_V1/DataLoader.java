package OUA.OUA_V1;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.repository.MemberRepository;
import OUA.OUA_V1.order.domain.Orders;
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
                150000, LocalDateTime.now().plusDays(7), ProductCategory.BEAUTY_CARE.getCategoryId(), dummyImageUrls);

        Product product2 = new Product(member3, "Mx Keys Mini", "테스트용 상품2입니다.", 80000,
                140000, LocalDateTime.now().plusDays(2), ProductCategory.DIGITAL_DEVICE.getCategoryId(), dummyImageUrls2);

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

        Orders orders = new Orders(member3, product, 100000);
        Orders orders2 = new Orders(member, product, 110000);
        Orders orders3 = new Orders(member2, product2, 90000);
        Orders orders4 = new Orders(member6, product2, 100000);
        Orders orders5 = new Orders(member, product3, 11000);
        Orders orders6 = new Orders(member2, product3, 12000);
        Orders orders7 = new Orders(member8, product4, 1510000);
        Orders orders8 = new Orders(member9, product4, 1600000);
        Orders orders9 = new Orders(member5, product5, 251000);
        Orders orders10 = new Orders(member7, product5, 252000);

        orderRepository.save(orders);
        orderRepository.save(orders2);
        orderRepository.save(orders3);
        orderRepository.save(orders4);
        orderRepository.save(orders5);
        orderRepository.save(orders6);
        orderRepository.save(orders7);
        orderRepository.save(orders8);
        orderRepository.save(orders9);
        orderRepository.save(orders10);

        product.updateHighestOrder(orders2.getId(), orders2.getOrderPrice());
        product2.updateHighestOrder(orders4.getId(), orders4.getOrderPrice());
        product3.updateHighestOrder(orders6.getId(), orders6.getOrderPrice());
        product4.updateHighestOrder(orders8.getId(), orders8.getOrderPrice());
        product5.updateHighestOrder(orders10.getId(), orders10.getOrderPrice());

    }
}
