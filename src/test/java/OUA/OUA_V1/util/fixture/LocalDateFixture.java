package OUA.OUA_V1.util.fixture;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateFixture {

    public static LocalDateTime oneDayAgo() {
        return LocalDateTime.now(fixedClock()).minusDays(1);
    }

    public static Clock fixedClock() {
        return Clock.fixed(Instant.parse("2025-03-01T02:00:00Z"), ZoneOffset.UTC);
    }

    public static LocalDateTime oneWeekAgo() {
        return LocalDateTime.now(fixedClock()).minusWeeks(1);
    }

    public static LocalDateTime oneDayLater() {
        return LocalDateTime.now(fixedClock()).plusDays(1);
    }

    public static LocalDateTime oneWeekLater() {
        return LocalDateTime.now(fixedClock()).plusWeeks(1);
    }
}
