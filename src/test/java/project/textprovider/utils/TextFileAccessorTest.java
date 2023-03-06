package project.textprovider.utils;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TextFileAccessorTest {
  private static final String TEST_INPUT_FILE = "inputs/in.txt";
  private static final int MAX_LINE_NUM = 1000;
  private static final int NUM_READ_LINES = 10;
  private static final int REPEATED_READ_LINE = 5;

  private static String inputFilePath;

  @BeforeAll
  static void setUp() {
    ClassLoader classLoader = TextFileAccessorTest.class.getClassLoader();

    inputFilePath = requireNonNull(classLoader.getResource(TEST_INPUT_FILE)).getPath();
  }

  @Test
  void createTextFileAccessorWithExistingFile() {
    assertDoesNotThrow(() -> new TextFileAccessor(inputFilePath));
  }

  @Test
  void readLineZero() throws IOException {
    try (TextFileAccessor fileAccessor = new TextFileAccessor(inputFilePath)) {
      assertNull(fileAccessor.read(0L));
    }
  }

  @Test
  void readBeyondMaxLineNum() throws IOException {
    try (TextFileAccessor fileAccessor = new TextFileAccessor(inputFilePath)) {
      assertNull(fileAccessor.read(MAX_LINE_NUM + 1));
    }
  }

  @Test
  void readValidLines() throws IOException {
    try (TextFileAccessor fileAccessor = new TextFileAccessor(inputFilePath)) {
      Random rand = new Random();
      for (int i = 0; i < NUM_READ_LINES; ++i) {
        long lineNum = 1 + rand.nextInt(MAX_LINE_NUM);

        String expectedLine = String.format("%d: this is line %d", lineNum, lineNum);
        assertEquals(expectedLine, fileAccessor.read(lineNum), "file accessor read mismatch");
      }
    }
  }

  @Test
  void readRepeatedlySameLine() throws IOException {
    try (TextFileAccessor fileAccessor = new TextFileAccessor(inputFilePath)) {
      String expectedLine =
          String.format("%d: this is line %d", REPEATED_READ_LINE, REPEATED_READ_LINE);
      assertEquals(
          expectedLine, fileAccessor.read(REPEATED_READ_LINE), "file accessor read mismatch");

      assertEquals(
          expectedLine, fileAccessor.read(REPEATED_READ_LINE), "file accessor read mismatch");
    }
  }
}
