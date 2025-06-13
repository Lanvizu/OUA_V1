package OUA.OUA_V1.util.fixture;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.domain.Product;

import java.time.LocalDateTime;

public class ProductFixture {

    public static Product createProduct1(Member member, String productName, int initialPrice, int buyNowPrice) {
        return new Product(member, productName, "테스트 상품1 입니다.", initialPrice, buyNowPrice,
                LocalDateTime.now().plusDays(7), 301, null);
    }
}
