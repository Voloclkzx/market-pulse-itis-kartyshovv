package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(Path path, List<Quote> quotes) {
        //  Подсказка: на каждую котировку нужно сохранить тикер, тип и валюту инструмента (чтобы
        //  потом восстановить его через InstrumentFactories.create), а также цену и изменение за
        //  период — этого достаточно, чтобы собрать Quote обратно.
        Path pathParent = path.getParent();
        if (pathParent != null) {
            try {
                Files.createDirectories(pathParent);
            } catch (IOException e) {
                throw new UncheckedIOException("Не удалось создать каталог: " + pathParent, e);
            }
        }
        List<QuoteFileDto> list = quotes.stream().map(QuoteFileMapper::toDto).toList();

        try {
            objectMapper.writeValue(path.toFile(), list);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать данные в файл: " + path, e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        if (Files.notExists(path)) {
            return Collections.emptyList();
        }
        try {
            List<QuoteFileDto> quotesDto = objectMapper.readValue(path.toFile(), new TypeReference<>() {
            });

            if (quotesDto == null) {
                throw new IOException("Ожидался JSON-массив стикеров, получен null");
            }

            return quotesDto.stream().map(QuoteFileMapper::toDomain).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось загрузить данные из файла: " + path, e);
        }
    }
}
