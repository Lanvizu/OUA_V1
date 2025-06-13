package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryCustom {
    Slice<Product> findByFiltersWithKeySet(String keyword, Boolean onSale,
                                           Integer categoryId, LocalDateTime lastCreatedDate, int size);
    Slice<Product> findByMemberIdWithKeySet(Long memberId, LocalDateTime localDateTime, int size);
    List<Product> findByEndTimeBeforeAndStatus(LocalDateTime endTime, ProductStatus status);
}
