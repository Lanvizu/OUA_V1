package OUA.OUA_V1.product.domain;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.exception.badRequest.ProductIllegalNameException;
import OUA.OUA_V1.product.exception.badRequest.ProductNameBlankException;
import OUA.OUA_V1.product.exception.badRequest.ProductNameLengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("상품 도메인 테스트")
public class ProductTest {

    private Member getTestMember() {
        return new Member("test@example.com", "홍길동", "길동이", "Password123!", "01012345678");
    }

    @DisplayName("유효한 값으로 Product 생성에 성공한다.")
    @ParameterizedTest
    @CsvSource({
            "책상, 책상 설명입니다, 10000, 20000",
            "노트북, 가성비 노트북입니다, 500000, 1000000",
            "볼펜, 좋은 펜입니다, 1000, 2000"
    })
    void createProductWithValidData(String name, String description, int initialPrice, int buyNowPrice) {
        Member member = getTestMember();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        Integer categoryId = 1;
        List<String> imageUrls = List.of("url1", "url2");

        assertDoesNotThrow(() -> new Product(
                member, name, description, initialPrice, buyNowPrice, endDate, categoryId, imageUrls
        ));
    }

    @DisplayName("이름이 빈 문자열이면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void createProductWithBlankName(String name) {
        Member member = getTestMember();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        assertThrows(ProductNameBlankException.class, () -> new Product(
                member, name, "설명", 10000, 20000, endDate, 1, List.of()
        ));
    }
    @DisplayName("이름 길이가 2 미만 또는 32 초과일 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "A",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"
    })
    void createProductWithInvalidNameLength(String name) {
        Member member = getTestMember();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        assertThrows(ProductNameLengthException.class, () -> new Product(
                member, name, "설명", 10000, 20000, endDate, 1, List.of()
        ));
    }

    @DisplayName("이름에 금지 문자가 포함되면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "상품|이름",       // pipe 포함
            "좋은\\상품"       // 백슬래시 포함
    })
    void createProductWithIllegalCharacters(String name) {
        Member member = getTestMember();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        assertThrows(ProductIllegalNameException.class, () -> new Product(
                member, name, "설명", 10000, 20000, endDate, 1, List.of()
        ));
    }
}
