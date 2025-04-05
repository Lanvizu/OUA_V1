package OUA.OUA_V1.product.repository;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.repository.query.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>,
        ProductRepositoryCustom {

}
