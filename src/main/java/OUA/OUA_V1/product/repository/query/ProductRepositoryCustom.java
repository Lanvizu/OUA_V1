package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findAllByFilters(String keyword, Boolean onSale, Integer categoryId, Pageable pageable);
}
