package project.textprovider.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LruCacheTest {
  private static final int CACHE_CAPACITY = 5;
  private static final String CACHE_OBJECT = "entry-";

  @Test
  void cacheDoesNotGrowBeyondCacheCapacity() {
    LruCache cache = new LruCache(CACHE_CAPACITY);

    assertEquals(0, cache.size(), "cache mismatch in size");
    for (long i = 0; i < CACHE_CAPACITY; ++i) {
      cache.put(i, CACHE_OBJECT + i);
    }
    assertEquals(CACHE_CAPACITY, cache.size(), "cache mismatch in size");

    long newKey = 1 + CACHE_CAPACITY;
    cache.put(newKey, CACHE_OBJECT + newKey);
    assertTrue(cache.containsKey(newKey), "cache mismatch in key membership");
    assertEquals(CACHE_CAPACITY, cache.size(), "cache mismatch in size");
  }

  @Test
  void cacheIsLru() {
    LruCache cache = new LruCache(CACHE_CAPACITY);

    assertEquals(0, cache.size(), "cache mismatch in size");
    for (long i = 0; i < CACHE_CAPACITY; ++i) {
      cache.put(i, CACHE_OBJECT + i);
    }
    assertEquals(CACHE_CAPACITY, cache.size(), "cache mismatch in size");

    long newKey = 1 + CACHE_CAPACITY;
    cache.put(newKey, CACHE_OBJECT + newKey);
    assertTrue(cache.containsKey(newKey), "cache mismatch in key membership");
    assertEquals(CACHE_OBJECT + newKey, cache.get(newKey), "cache mismatch for key: " + newKey);
    assertEquals(CACHE_CAPACITY, cache.size(), "cache mismatch in size");
    assertFalse(cache.containsKey(0L), "cache mismatch in key membership");
  }

  @Test
  void putObjectInCache() {
    LruCache cache = new LruCache(CACHE_CAPACITY);

    for (long i = 0; i < CACHE_CAPACITY; ++i) {
      cache.put(i, CACHE_OBJECT + i);
    }

    for (long i = 0; i < CACHE_CAPACITY; ++i) {
      assertTrue(cache.containsKey(i), "cache mismatch in key membership");
      assertEquals(CACHE_OBJECT + i, cache.get(i), "cache mismatch for key: " + i);
    }

    long newKey = 1 + CACHE_CAPACITY;
    cache.put(newKey, CACHE_OBJECT + newKey);
    assertTrue(cache.containsKey(newKey), "cache mismatch in key membership");
    assertEquals(CACHE_OBJECT + newKey, cache.get(newKey), "cache mismatch for key: " + newKey);
  }

  @Test
  void returnNullIfKeyAbsent() {
    LruCache cache = new LruCache(CACHE_CAPACITY);

    long keyPresent = 0L;
    cache.put(keyPresent, CACHE_OBJECT + keyPresent);
    assertTrue(cache.containsKey(keyPresent), "cache mismatch in key membership");
    assertNotNull(cache.get(keyPresent), "cache mismatch in return object");

    long keyAbsent = 1L;
    assertFalse(cache.containsKey(keyAbsent), "cache mismatch in key membership");
    assertNull(cache.get(keyAbsent), "cache mismatch in return object");
  }

  @Test
  void cacheCanClearItself() {
    LruCache cache = new LruCache(CACHE_CAPACITY);

    long keyPresent = 0L;
    cache.put(keyPresent, CACHE_OBJECT + keyPresent);
    assertEquals(1, cache.size(), "cache mismatch in size");

    cache.clear();
    assertEquals(0, cache.size(), "cache mismatch in size");
  }
}
