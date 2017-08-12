package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.logging.log4j.Logger;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class TestClassExecutor {

  private final Logger logger;

  public TestClassExecutor(Logger logger) {
    this.logger = checkNotNull(logger, "logger==null!");
  }

  public TestResults runTests(final Class<?> testClazz) throws InitializationError {
    BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClazz);
    TestResults res = new TestResults(logger);
    runner.run(res);
    return res;
  }

}
