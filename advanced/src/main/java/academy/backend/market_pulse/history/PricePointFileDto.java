package academy.backend.market_pulse.history;

import java.math.BigDecimal;

public record PricePointFileDto(
    String ticker,
    String timestamp,
    BigDecimal price
) {
}
