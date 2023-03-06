package project.textproviderapp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.textprovider.TextProvider;
import project.textprovider.exceptions.TextProviderException;

public class TextProviderApp {
  private static final Logger logger = LoggerFactory.getLogger(TextProviderApp.class);
  private static final int STATUS_CODE_USAGE = 1;
  private static final int STATUS_CODE_FILE = 2;

  public static void main(String[] args) throws TextProviderException {
    if (args.length < 1) {
      showUsage();
      System.exit(STATUS_CODE_USAGE);
    }

    final String inputFile = args[0];
    if (!Files.isReadable(Paths.get(inputFile))) {
      logger.error("Input file {} cannot be read", inputFile);
      System.exit(STATUS_CODE_FILE);
    }

    try (TextProvider textProvider = new TextProvider(inputFile)) {
      List<Integer> evenLineNums = new ArrayList<>();
      List<Integer> oddLineNums = new ArrayList<>();
      for (int i = 1; i < args.length; ++i) {
        int num = Integer.parseInt(args[i]);

        if (num % 2 == 0) {
          evenLineNums.add(num);
        } else {
          oddLineNums.add(num);
        }
      }

      final ExecutorService executorService = Executors.newFixedThreadPool(2);
      Future<Integer> evenFuture =
          executorService.submit(
              () -> {
                int counter = 0;
                for (Integer lineNum : evenLineNums) {
                  try {
                    String line = textProvider.getLine(lineNum);
                    logger.info("{} --> [{}]", lineNum, line);
                  } catch (TextProviderException e) {
                    ++counter;
                    logger.error("line {} failed: {}", lineNum, e.getMessage());
                  }
                }
                return counter;
              });

      Future<Integer> oddFuture =
          executorService.submit(
              () -> {
                int counter = 0;
                for (Integer lineNum : oddLineNums) {
                  try {
                    String line = textProvider.getLine(lineNum);
                    logger.info("{} --> [{}]", lineNum, line);
                  } catch (TextProviderException e) {
                    ++counter;
                    logger.error("line {} failed: {}", lineNum, e.getMessage());
                  }
                }
                return counter;
              });

      logger.info("even list failures: {}", evenFuture.get());
      logger.info("odd list failures: {}", oddFuture.get());

      executorService.shutdown();
    } catch (InterruptedException | ExecutionException e) {
      logger.error("something went wrong", e);
    }
  }

  private static void showUsage() {
    logger.error("Usage: TextProvisionerApp <text-file>\n");
  }
}
