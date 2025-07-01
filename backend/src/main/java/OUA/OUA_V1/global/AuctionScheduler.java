package OUA.OUA_V1.global;

import OUA.OUA_V1.product.facade.ProductLockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {
    private final ProductLockFacade productLockFacade;

    @Scheduled(cron = "0 0 4 * * *") //4시에 업데이트 (실 서비스가 아니므로 길게 설정해놨습니다.)
    public void checkExpiredAuctions() {
        productLockFacade.finalizeExpiredAuctions();
    }
}