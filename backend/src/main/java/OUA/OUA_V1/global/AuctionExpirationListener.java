package OUA.OUA_V1.global;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class AuctionExpirationListener extends KeyExpirationEventMessageListener {

    private final ApplicationEventPublisher eventPublisher;

    public AuctionExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            ApplicationEventPublisher eventPublisher
    ) {
        super(listenerContainer);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith("auction:product:")) {
            Long productId = Long.parseLong(expiredKey.split(":")[2]);
            eventPublisher.publishEvent(new AuctionExpiredEvent(productId));
        }
    }
}
