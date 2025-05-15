package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.domain.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Product> findByFiltersWithKeySet(String keyword, Boolean onSale,
                                                  Integer categoryId, LocalDateTime lastCreatedDate, int size) {
        QProduct product = QProduct.product;

        BooleanBuilder predicate = new BooleanBuilder()
                .and(product.deleted.isFalse());

        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(product.name.containsIgnoreCase(keyword));
        }

        if (onSale != null) {
            predicate.and(product.status.eq(ProductStatus.ACTIVE));
        }

        if (categoryId != null) {
            predicate.and(product.categoryId.eq(categoryId));
        }

        if (lastCreatedDate != null) {
            predicate.and(product.createdDate.lt(lastCreatedDate));
        }

        List<Product> products = queryFactory
                .selectFrom(product)
                .where(predicate)
                .orderBy(product.createdDate.desc(), product.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = products.size() > size;

        if (hasNext) {
            products.removeLast();
        }

        return new SliceImpl<>(products, PageRequest.of(0, size), hasNext);
    }

    @Override
    public Slice<Product> findByMemberIdWithKeySet(Long memberId, LocalDateTime lastCreatedDate, int size) {
        QProduct product = QProduct.product;

        BooleanBuilder predicate = new BooleanBuilder()
                .and(product.member.id.eq(memberId))
                .and(product.deleted.isFalse());

        if (lastCreatedDate != null) {
            predicate.and(product.createdDate.lt(lastCreatedDate));
        }

        List<Product> products = queryFactory
                .selectFrom(product)
                .where(predicate)
                .orderBy(product.createdDate.desc(), product.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = products.size() > size;

        if (hasNext) {
            products.removeLast();
        }

        return new SliceImpl<>(products, PageRequest.of(0, size), hasNext);
    }
}
