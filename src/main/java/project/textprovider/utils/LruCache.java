package project.textprovider.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache {
  private static final float LOAD_FACTOR = 0.90F;
  private final Map<Long, String> cache;

  public LruCache(int capacity) {
    this.cache =
        Collections.synchronizedMap(
            new LinkedHashMap<>(capacity, LOAD_FACTOR, true) {
              @Override
              protected boolean removeEldestEntry(Map.Entry<Long, String> entry) {
                return size() > capacity;
              }
            });
  }

  public void clear() {
    cache.clear();
  }

  public boolean containsKey(Long key) {
    return cache.containsKey(key);
  }

  public String get(Long key) {
    return cache.get(key);
  }

  public void put(Long key, String value) {
    cache.put(key, value);
  }

  public int size() {
    return cache.size();
  }
}
