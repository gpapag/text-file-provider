package project.textprovider;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import project.textprovider.exceptions.TextProviderException;

class TextProviderTest {
  private static final String TEST_INPUT_FILE = "inputs/in.txt";
  private static final int MAX_LINE_NUM = 1000;
  private static final int NUM_READ_LINES = 10;
  private static final int REPEATED_READ_LINE = 5;

  private static String inputFilePath;

  @BeforeAll
  static void setUp() {
    ClassLoader classLoader = TextProviderTest.class.getClassLoader();

    inputFilePath = requireNonNull(classLoader.getResource(TEST_INPUT_FILE)).getPath();
  }

  @Test
  void createTextProviderWithExistingFile() {
    assertDoesNotThrow(() -> new TextProvider(inputFilePath));
  }

  @Test
  void createTextProviderWithNonExistingFile() {
    assertThrows(TextProviderException.class, () -> new TextProvider(TEST_INPUT_FILE));
  }

  @Test
  void getLineZero() throws TextProviderException {
    try (TextProvider textProvider = new TextProvider(inputFilePath)) {
      assertThrows(TextProviderException.class, () -> textProvider.getLine(0L));
    }
  }

  @Test
  void getLineBeyondMaxLine() throws TextProviderException {
    try (TextProvider textProvider = new TextProvider(inputFilePath)) {
      assertThrows(TextProviderException.class, () -> textProvider.getLine(MAX_LINE_NUM + 1));
    }
  }

  @Test
  void getValidLines() throws TextProviderException {
    try (TextProvider textProvider = new TextProvider(inputFilePath)) {
      Random rand = new Random();
      for (int i = 0; i < NUM_READ_LINES; ++i) {
        long lineNum = 1 + rand.nextInt(MAX_LINE_NUM);

        String expectedLine = String.format("%d: this is line %d", lineNum, lineNum);
        assertEquals(
            expectedLine, textProvider.getLine(lineNum), "text provider get line mismatch");
      }
    }
  }

  @Test
  void getRepeatedlySameLine() throws TextProviderException {
    try (TextProvider textProvider = new TextProvider(inputFilePath)) {
      String expectedLine =
          String.format("%d: this is line %d", REPEATED_READ_LINE, REPEATED_READ_LINE);
      assertEquals(
          expectedLine,
          textProvider.getLine(REPEATED_READ_LINE),
          "text provider get line mismatch");

      assertEquals(
          expectedLine,
          textProvider.getLine(REPEATED_READ_LINE),
          "text provider get line mismatch");
    }
  }
}
