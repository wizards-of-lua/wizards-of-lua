package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class TestMethodExecutor {

  private final Logger logger;

  public TestMethodExecutor(Logger logger) {
    this.logger = checkNotNull(logger,"logger==null!");
  }

  public TestResults runTest(final Class<?> testClazz, final String methodName)
      throws InitializationError {
    BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClazz) {
      @Override
      protected List<FrameworkMethod> computeTestMethods() {
        try {
          Method method = testClazz.getMethod(methodName);
          return Arrays.asList(new FrameworkMethod(method));

        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
    TestResults res = new TestResults(logger);
    runner.run(res);
    return res;
  }

}
