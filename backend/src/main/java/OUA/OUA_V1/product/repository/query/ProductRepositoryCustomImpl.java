package OUA.OUA_V1.product.repository.query;

import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAllByFilters(String keyword, Boolean onSale,
                                          Integer categoryId, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanExpression predicate = createPredicate(product, keyword, onSale, categoryId);

        List<Product> products = queryFactory
                .selectFrom(product)
                .where(predicate)
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(
                queryFactory
                        .select(product.countDistinct())
                        .from(product)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(products, pageable, totalCount);
    }

    private BooleanExpression createPredicate(
            QProduct product,
            String keyword,
            Boolean onSale,
            Integer categoryId
    ) {
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        predicate = predicate.and(containsKeyword(product, keyword));
        predicate = predicate.and(isOnSaleEqual(product, onSale));
        predicate = predicate.and(isCategoryEqual(product, categoryId));

        return predicate;
    }

    private BooleanExpression containsKeyword(QProduct product, String keyword) {
        return (keyword != null && !keyword.isEmpty())
                ? product.name.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression isOnSaleEqual(QProduct product, Boolean onSale) {
        return (onSale != null)
                ? product.onSale.eq(onSale)
                : null;
    }

    private BooleanExpression isCategoryEqual(QProduct product, Integer categoryId) {
        return (categoryId != null)
                ? product.categoryId.eq(categoryId)
                : null;
    }

    @Override
    public Page<Product> findAllByMemberId(Long memberId, Pageable pageable) {
        QProduct product = QProduct.product;

        List<Product> products = queryFactory
                .selectFrom(product)
                .where(product.member.id.eq(memberId))
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(
                queryFactory
                        .select(product.count())
                        .from(product)
                        .where(product.member.id.eq(memberId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(products, pageable, totalCount);
    }
}
