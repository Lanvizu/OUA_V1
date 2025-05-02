package OUA.OUA_V1.global;

import OUA.OUA_V1.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventHandler {

    private final ProductFacade productFacade;

    @EventListener
    public void handleAuctionExpired(AuctionExpiredEvent event) {
        productFacade.finalizeAuction(event.getProductId());
    }
}
