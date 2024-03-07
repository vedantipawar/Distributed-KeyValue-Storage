import java.util.HashMap;
import java.util.LinkedHashSet;

public class LFUCache {
    private HashMap<String, String> keyValueMap;
    private HashMap<String, Integer> keyFreqMap;
    private HashMap<Integer, LinkedHashSet<String>> freqKeysMap;
    private int capacity;
    private int minFrequency;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.keyValueMap = new HashMap<>();
        this.keyFreqMap = new HashMap<>();
        this.freqKeysMap = new HashMap<>();
        this.freqKeysMap.put(1, new LinkedHashSet<>());
        this.minFrequency = -1;
    }

    public String get(String key) {
        if (!keyValueMap.containsKey(key)) {
            return null; // Not found
        }
        // Increase the frequency of the key
        int freq = keyFreqMap.get(key);
        keyFreqMap.put(key, freq + 1);
        freqKeysMap.get(freq).remove(key);

        if (freq == minFrequency && freqKeysMap.get(freq).size() == 0) {
            minFrequency++;
        }

        freqKeysMap.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        return keyValueMap.get(key);
    }

    public void put(String key, String value) {
        if (capacity <= 0) {
            return;
        }
        if (keyValueMap.containsKey(key)) {
            keyValueMap.put(key, value);
            get(key); // Update frequency
            return;
        }
        if (keyValueMap.size() >= capacity) {
            String evictKey = freqKeysMap.get(minFrequency).iterator().next();
            freqKeysMap.get(minFrequency).remove(evictKey);
            keyValueMap.remove(evictKey);
            keyFreqMap.remove(evictKey);
        }
        keyValueMap.put(key, value);
        keyFreqMap.put(key, 1);
        minFrequency = 1;
        freqKeysMap.get(1).add(key);
    }
}
