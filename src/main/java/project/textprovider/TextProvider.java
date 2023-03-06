package project.textprovider;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.NonNull;
import project.textprovider.exceptions.TextProviderException;
import project.textprovider.utils.LruCache;
import project.textprovider.utils.TextFileAccessor;

public class TextProvider implements Closeable {
  private static final int DEFAULT_CACHE_SIZE_OBJECTS = 1024 * 1024;

  private final LruCache cache;
  private final TextFileAccessor fileAccessor;

  public TextProvider(@NonNull String inputFilename) throws TextProviderException {
    this(inputFilename, DEFAULT_CACHE_SIZE_OBJECTS);
  }

  public TextProvider(@NonNull String inputFilename, int cacheSize) throws TextProviderException {
    if (!Files.isReadable(Paths.get(inputFilename))) {
      String msg = String.format("text file %s is not readable", inputFilename);
      throw new TextProviderException(msg);
    }

    if (cacheSize <= 0) {
      throw new TextProviderException("cache size should be positive");
    }

    this.cache = new LruCache(cacheSize);
    this.fileAccessor = new TextFileAccessor(inputFilename);
  }

  public String getLine(long lineNum) throws TextProviderException {
    if (lineNum <= 0) {
      throw new TextProviderException("line number should be greater than 0");
    }

    String lineInCache = cache.get(lineNum);
    if (lineInCache != null) {
      return lineInCache;
    }

    String line;
    try {
      line = fileAccessor.read(lineNum);
      if (line == null) {
        String msg = String.format("line %d does not exist in the file ", lineNum);
        throw new TextProviderException(msg);
      }
      cache.put(lineNum, line);
    } catch (IOException e) {
      String msg = String.format("cannot read line %d from file ", lineNum);
      throw new TextProviderException(msg, e);
    }

    return line;
  }

  @Override
  public void close() {
    cache.clear();
    fileAccessor.close();
  }
}
