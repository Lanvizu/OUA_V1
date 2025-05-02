package OUA.OUA_V1.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionExpiredEvent {
    private Long productId;
}
