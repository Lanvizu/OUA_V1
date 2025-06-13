package OUA.OUA_V1.global;

import OUA.OUA_V1.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {
    private final ProductFacade productFacade;

    @Scheduled(fixedRate = 3600000) //1시간 (실 서비스가 아니므로 길게 설정해놨습니다.)
    public void checkExpiredAuctions() {
        productFacade.finalizeExpiredAuctions();
    }
}
