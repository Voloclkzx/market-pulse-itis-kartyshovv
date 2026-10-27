package academy.backend.market_pulse.repository.store.impl;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;

public class QuoteFileMapper {
    public static QuoteFileDto toDto(Quote quote) {
        Instrument instrument = quote.getInstrument();
        return new QuoteFileDto(
            instrument.getTicker(),
            instrument.getName(),
            instrument.getType(),
            instrument.getCurrency(),
            quote.getPrice(),
            quote.getChangePercent()
        );
    }

    public static Quote toDomain(QuoteFileDto quoteFileDto) {
        return new Quote(
            InstrumentFactories.create(
                quoteFileDto.type(),
                quoteFileDto.ticker(),
                quoteFileDto.name(),
                quoteFileDto.currency()
            ),
            quoteFileDto.price(),
            quoteFileDto.changePercent()
        );
    }
}
