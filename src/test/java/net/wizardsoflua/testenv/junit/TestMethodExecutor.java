package net.wizardsoflua.testenv.junit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class TestMethodExecutor {

  public TestResult runTest(final Class<?> testClazz, final String methodName)
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
    TestResult res = new TestResult();
    runner.run(res);
    return res;
  }

}
