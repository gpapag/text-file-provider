package project.textprovider.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TextFileAccessor implements Closeable {
  private static final long FIRST_LINE_NUM = 1L;
  private static final long FIRST_LINE_BYTE_OFFSET = 0L;

  private final ConcurrentNavigableMap<Long, Long> lineToByteOffset;
  private final File file;

  public TextFileAccessor(String inputFilename) {
    this.lineToByteOffset = new ConcurrentSkipListMap<>();
    this.file = new File(inputFilename);

    lineToByteOffset.put(FIRST_LINE_NUM, FIRST_LINE_BYTE_OFFSET);
  }

  public String read(long lineNum) throws IOException {
    if (lineToByteOffset.containsKey(lineNum)) {
      return getLineFromOffset(lineToByteOffset.get(lineNum));
    }

    Map.Entry<Long, Long> startingSearchPoint = lineToByteOffset.floorEntry(lineNum);
    Long startingLineNum =
        startingSearchPoint == null ? FIRST_LINE_NUM : startingSearchPoint.getKey();
    Long startingByteOffset =
        startingSearchPoint == null ? FIRST_LINE_BYTE_OFFSET : startingSearchPoint.getValue();

    return getLineFromClosestOffset(lineNum, startingLineNum, startingByteOffset);
  }

  @Override
  public void close() {
    lineToByteOffset.clear();
  }

  private String getLineFromOffset(Long position) throws IOException {
    try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
      randomAccessFile.seek(position);
      return randomAccessFile.readLine();
    }
  }

  private String getLineFromClosestOffset(Long targetLineNum, Long lineNum, Long position)
      throws IOException {
    try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
      long bytesRead = 0L;
      String currentLine = null;
      randomAccessFile.seek(position);

      for (long i = lineNum; i != 1 + targetLineNum; ++i) {
        currentLine = randomAccessFile.readLine();
        if (currentLine == null) {
          return currentLine;
        }
        bytesRead += currentLine.getBytes(StandardCharsets.US_ASCII).length;
        bytesRead += 1; // '\n'
        randomAccessFile.seek(position + bytesRead);
        lineToByteOffset.put(1 + i, position + bytesRead);
      }

      return currentLine;
    }
  }
}
