package repository;

import structured_files.DailyDocument;
import java.util.HashMap;
import java.util.Map;

public class MarketDataRepository {
    private final Map<String, Map<String, DailyDocument<?>>> documents = new HashMap<>();
}
