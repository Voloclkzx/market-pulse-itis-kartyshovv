package academy.backend.market_pulse.repository.store.impl;

import academy.backend.market_pulse.model.Currency;

import java.math.BigDecimal;

public record QuoteFileDto(
    String ticker,
    String name,
    String type,
    Currency currency,
    BigDecimal price,
    BigDecimal changePercent
) {
}
