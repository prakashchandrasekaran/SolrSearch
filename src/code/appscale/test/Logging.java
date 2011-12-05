package code.appscale.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(Logging.class);
    logger.info("Hello World");
  }
}
