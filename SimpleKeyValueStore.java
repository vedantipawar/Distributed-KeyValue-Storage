import java.util.HashMap;
import java.util.Map;

public class SimpleKeyValueStore implements KeyValueStore {
    private final Map<String, String> store;

    public SimpleKeyValueStore() {
        this.store = new HashMap<>();
    }

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }

    @Override
    public String get(String key) {
        return store.getOrDefault(key, null);
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }

    @Override
    public void update(String key, String value) {
        // Since HashMap's put method already updates the value for an existing key,
        // this can directly call put method.
        put(key, value);
    }
}
