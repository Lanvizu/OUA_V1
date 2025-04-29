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

import java.time.LocalDateTime;

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

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    public Order(Member member, Product product, int orderPrice) {
        this.member = member;
        this.product = product;
        this.orderPrice = orderPrice;
    }

    public void confirmOrder(){
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public void updateOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    @Override
    public boolean isAuthorizedBy(Member member) {
        return this.member.equals(member);
    }
}
