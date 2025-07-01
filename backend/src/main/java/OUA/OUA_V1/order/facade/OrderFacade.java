package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrdersService ordersService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public void create(Member member, Product product, int orderPrice) {
        Orders orders = ordersService.createOrder(member, product, orderPrice);
        productService.updateHighestOrder(product, orders.getId(), orders.getOrderPrice());
    }

    @Transactional
    public void updatePrice(Product product, Orders orders, int orderPrice) {
        ordersService.updateOrderPrice(orders, orderPrice);
        productService.updateHighestOrder(product, orders.getId(), orders.getOrderPrice());
    }

    @Transactional
    public void buyNow(Optional<Orders> existingOrderOpt, Product product, Long memberId) {
        Orders confirmedOrders = resolveConfirmedOrder(existingOrderOpt, product, memberId);
        // 이전 주문이 없는 경우 새로운 즉시 구매 주문을 생성
        // 경매 종료 + 최고 입찰가 업데이트
        productService.soldOutAndUpdateOrder(product, confirmedOrders.getId(), confirmedOrders.getOrderPrice());
        ordersService.failOtherOrders(product.getId(), confirmedOrders.getId());
    }

    private Orders resolveConfirmedOrder(Optional<Orders> existingOrderOpt, Product product, Long memberId) {
        return existingOrderOpt
                .map(order -> ordersService.updateBuyNowOrder(order, product))
                .orElseGet(() -> {
                    Member member = memberService.findById(memberId);
                    return ordersService.buyNowOrder(member, product);
                });
    }

    @Transactional
    public void cancel(Orders activeOrder, Product product, Long orderId) {

        // 해당 메서드 내의 트랜잭션 커밋이 끝나고 최신 상태에서 조회 가능
        ordersService.cancelOrder(activeOrder);

        // 최고 입찰가인 경우 다음 최고 입찰가로 업데이트.
        if (product.isHighestOrder(orderId)) {
            Optional<Orders> highestOrder = ordersService.findHighestOrder(product.getId());
            highestOrder.ifPresentOrElse(
                    h -> productService.updateHighestOrder(product, h.getId(), h.getOrderPrice()),
                    () -> productService.resetHighestOrder(product)
            );
        }
    }
}
