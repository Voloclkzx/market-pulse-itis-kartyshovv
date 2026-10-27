package academy.backend.market_pulse.history.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;
import academy.backend.market_pulse.history.PricePointFileDto;
import academy.backend.market_pulse.history.PricePointFileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FILE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSSSSSSSS")
            .withZone(ZoneOffset.UTC);

    @Override
    public Path append(Path directory, PricePoint point) {
        //  Подсказка: имя файла на основе point.timestamp() даёт сортировку по имени = сортировку
        //  по времени.
        try {
            Files.createDirectories(directory);

            PricePointFileDto dto = PricePointFileMapper.toDto(point);

            String timestamp = FILE_TIME_FORMAT.format(point.timestamp());
            String safeTicker = point.ticker().replaceAll("[^a-zA-Z0-9_-]", "_");

            String prefix = "price-" + timestamp + "-" + safeTicker + "-";

            Path file = Files.createTempFile(directory, prefix, ".json");

            objectMapper.writeValue(file.toFile(), dto);

            return file;
        } catch (IOException e) {
            throw new UncheckedIOException(
                "Не удалось сохранить историю в каталоге: " + directory, e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        if (Files.notExists(directory)) {
            return Collections.emptyList();
        }

        List<PricePoint> points = new ArrayList<>();

        try (Stream<Path> files = Files.list(directory)) {
            List<Path> historyFiles = files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith("price-"))
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .toList();

            for (Path file : historyFiles) {
                PricePointFileDto dto = objectMapper.readValue(file.toFile(), PricePointFileDto.class);

                if (dto == null) {
                    throw new IOException("Ожидалась точка истории, получен null: " + file);
                }

                PricePoint point = PricePointFileMapper.toDomain(dto);

                if (point.ticker().equals(ticker)) {
                    points.add(point);
                }
            }

            return points;

        } catch (IOException e) {
            throw new UncheckedIOException(
                "Не удалось загрузить историю из каталога: " + directory, e);
        }
    }
}
