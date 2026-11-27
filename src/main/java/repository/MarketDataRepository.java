package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import structuredfiles.DailyDocument;
import structuredfiles.Interval;
import structuredfiles.specificdocuments.DamPricesPredictionDocument;
import structuredfiles.specificdocuments.DamPricesDocument;
import structuredfiles.specificdocuments.GridLoadDocument;
import structuredfiles.specificdocuments.GridLoadPredictionDocument;
import structuredfiles.specificdocuments.ImbalancePricesDocument;
import structuredfiles.specificdocuments.ImbalanceVolumesDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.TreeMap;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MarketDataRepository {
    private static final int INTERVAL_MINUTES = 15;
    private static final int INTERVALS_PER_HOUR = 4;

    private final Map<String, TreeMap<String, DailyDocument<?>>> storage = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();


    private DailyDocument<?> getFromTemplate(final String dataType) {
        try {
            return switch (dataType) {
                case "DAM_PRICE" -> mapper.readValue(
                        new File("template/dam_prices.json"),
                        DamPricesDocument.class);
                case "DAM_PRICE_PREDICTION" -> mapper.readValue(
                        new File("template/dam_prices_prediction.json"),
                        DamPricesPredictionDocument.class);
                case "GRID_LOAD" -> mapper.readValue(
                        new File("template/grid_load.json"),
                        GridLoadDocument.class);
                case "GRID_LOAD_PREDICTION" -> mapper.readValue(
                        new File("template/grid_load_prediction.json"),
                        GridLoadPredictionDocument.class);
                case "IMBALANCE_PRICE" -> mapper.readValue(
                        new File("template/imbalance_prices.json"),
                        ImbalancePricesDocument.class);
                case "IMBALANCE_VOLUME" -> mapper.readValue(
                        new File("template/imbalance_volumes.json"),
                        ImbalanceVolumesDocument.class);
                default -> throw new IllegalArgumentException("Unknown dataType: " + dataType);
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template for " + dataType, e);
        }
    }

    public DailyDocument getOrCreateDocument(final String dataType, final String date) {
        if (!storage.containsKey(dataType)) {
            storage.put(dataType, new TreeMap<>());
        }
        Map<String, DailyDocument<?>> documentsByDate = storage.get(dataType);
        if (!documentsByDate.containsKey(date)) {
            DailyDocument<?> templateDocument = getFromTemplate(dataType);
            documentsByDate.put(date, templateDocument);
        }
        return documentsByDate.get(date);
    }

    public void insert(final String dataType, final String date, final Interval point) {
        int intervalIndex = point.getIntervalIndex();
        DailyDocument doc = getOrCreateDocument(dataType, date);
        List points = doc.getPoints();
        if (intervalIndex >= 1 && intervalIndex <= points.size()) {
            points.set(intervalIndex - 1, point);
        }
    }

    public void update(final String dataType, final String date, final Interval point) {
        insert(dataType, date, point);
    }

    public DailyDocument<?> select(final String dataType, final String date) {
        Map<String, DailyDocument<?>> documentsByDate = storage.get(dataType);
        if (documentsByDate == null) {
            return null;
        }
        return documentsByDate.get(date);
    }

    public Interval selectInterval(final String dataType, final String date,
                                   final int intervalIndex) {
        DailyDocument<?> doc = select(dataType, date);
        if (doc == null || doc.getPoints() == null) {
            return null;
        }
        if (intervalIndex < 1 || intervalIndex > doc.getPoints().size()) {
            return null;
        }
        return (Interval) doc.getPoints().get(intervalIndex - 1);
    }

    public Interval findPreviousInterval(final String dataType,
                                         final String date,
                                         final int intervalIndex) {
        TreeMap<String, DailyDocument<?>> docs = storage.get(dataType);
        if (docs == null || docs.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, DailyDocument<?>> entry
                : docs.headMap(date, true).descendingMap().entrySet()) {
            List<?> points = entry.getValue().getPoints();
            if (points == null) {
                continue;
            }

            int startIdx = entry.getKey().equals(date) ? intervalIndex - 2 : points.size() - 1;

            for (int i = startIdx; i >= 0; i--) {
                Object obj = points.get(i);
                if (obj instanceof Interval interval && interval.hasData()) {
                    return interval;
                }
            }
        }
        return null;
    }
    public void clearInterval(final String dataType,
                              final String date,
                              final int intervalIndex) {
        DailyDocument<?> doc = select(dataType, date);
        if (doc == null || doc.getPoints() == null) {
            return;
        }
        if (intervalIndex >= 1 && intervalIndex <= doc.getPoints().size()) {
            doc.getPoints().set(intervalIndex - 1, null);
        }
    }

    public LocalDateTime findLatestIntervalStart(final String dataType,
                                                 final LocalDateTime maxSlotStart) {
        TreeMap<String, DailyDocument<?>> docs = storage.get(dataType);
        if (docs == null || docs.isEmpty()) {
            return null;
        }

        String maxDate = maxSlotStart.toLocalDate().toString();
        int maxIndex = (maxSlotStart.getHour() * INTERVALS_PER_HOUR)
                + (maxSlotStart.getMinute() / INTERVAL_MINUTES) + 1;

        for (Map.Entry<String, DailyDocument<?>> entry
                : docs.headMap(maxDate, true).descendingMap().entrySet()) {
            List<?> points = entry.getValue().getPoints();
            if (points == null) {
                continue;
            }

            int startIdx = entry.getKey().equals(maxDate)
                    ? Math.min(maxIndex - 1, points.size() - 1)
                    : points.size() - 1;

            for (int i = startIdx; i >= 0; i--) {
                Object obj = points.get(i);
                if (obj instanceof Interval interval && interval.hasData()) {
                    LocalDate d = LocalDate.parse(entry.getKey());
                    LocalTime t = LocalTime.of(i / INTERVALS_PER_HOUR,
                            (i % INTERVALS_PER_HOUR) * INTERVAL_MINUTES);
                    return LocalDateTime.of(d, t);
                }
            }
        }
        return null;
    }
}
