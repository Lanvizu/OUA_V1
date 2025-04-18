package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderCreateRequest;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.exception.badRequest.OrderAlreadyExistsException;
import OUA.OUA_V1.order.exception.badRequest.OrderOnOwnProductException;
import OUA.OUA_V1.order.service.OrderService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Long create(Long memberId, Long productId, OrderCreateRequest request) {
        if (orderService.existsByMemberIdAndProductId(memberId, productId)) {
            throw new OrderAlreadyExistsException();
        }

        Product product = productService.findById(productId);
        if(product.getMember().getId().equals(memberId)){
            throw new OrderOnOwnProductException();
        }

        Member member = memberService.findById(memberId);

        Order order = orderService.createOrder(member, product, request.orderPrice());
        return order.getId();
    }
}
