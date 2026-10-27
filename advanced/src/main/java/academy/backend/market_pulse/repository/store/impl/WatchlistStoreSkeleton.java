package academy.backend.market_pulse.repository.store.impl;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, построчный текст и т.д.); главное,
 * чтобы {@code load(save(tickers))} возвращал тот же набор тикеров в том же порядке. Проверьте себя
 * тестом {@code WatchlistStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class WatchlistStoreSkeleton implements WatchlistStore {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(Path path, Set<String> tickers) {
        Path pathParent = path.getParent();
        if (pathParent != null) {
            try {
                Files.createDirectories(pathParent);
            } catch (IOException e) {
                throw new UncheckedIOException("Не удалось создать каталог: " + pathParent, e);
            }
        }

        try {
            objectMapper.writeValue(path.toFile(), tickers);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать данные в файл: " + path, e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        if (Files.notExists(path)) {
            return Collections.emptySet();
        }
        try {
            Set<String> tickers = objectMapper.readValue(path.toFile(), new TypeReference<LinkedHashSet<String>>() {});

            if (tickers == null) {
                throw new IOException("Ожидался JSON-массив стикеров, получен null");
            }

            return tickers;
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось загрузить данные из файла: " + path, e);
        }
    }
}
