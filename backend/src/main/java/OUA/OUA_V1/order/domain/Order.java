package OUA.OUA_V1.order.domain;

import OUA.OUA_V1.BaseEntity;
import OUA.OUA_V1.auth.util.SecureResource;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "orders")
public class Order extends BaseEntity implements SecureResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.ACTIVE;

    public Order(Member member, Product product, int orderPrice) {
        this.member = member;
        this.product = product;
        this.orderPrice = orderPrice;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    @Override
    public boolean isAuthorizedBy(Member member) {
        return this.member.equals(member);
    }
}
