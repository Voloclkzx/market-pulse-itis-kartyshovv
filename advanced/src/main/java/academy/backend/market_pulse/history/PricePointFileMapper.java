package academy.backend.market_pulse.history;

import java.time.Instant;

public final class PricePointFileMapper {

    private PricePointFileMapper() {
    }

    public static PricePointFileDto toDto(PricePoint point) {
        return new PricePointFileDto(
            point.ticker(),
            point.timestamp().toString(),
            point.price()
        );
    }

    public static PricePoint toDomain(PricePointFileDto dto) {
        return new PricePoint(
            dto.ticker(),
            Instant.parse(dto.timestamp()),
            dto.price()
        );
    }
}
