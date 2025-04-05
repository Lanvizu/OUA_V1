package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.domain.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Product> findAllByStatus(ProductStatus status, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanExpression forSale = isStatusEqual(status);

        List<Product> products = queryFactory
                .selectFrom(product)
                .where(forSale)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(product.count())
                .from(product)
                .where(forSale)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0;

        return new PageImpl<>(products, pageable, total);
    }

    /**
     * 상태 조건을 생성하는 메서드
     */
    private BooleanExpression isStatusEqual(ProductStatus status) {
        if (status == null) {
            return null; // 조건이 없으면 전체 조회
        }
        QProduct product = QProduct.product;
        return product.status.eq(status);
    }
}
