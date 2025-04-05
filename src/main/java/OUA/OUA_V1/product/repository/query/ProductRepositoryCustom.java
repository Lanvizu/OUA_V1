package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findAllByStatus(ProductStatus status, Pageable pageable);
}
