package net.wizardsoflua.testenv;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class TestMethodExecutor {

  public Result runTest(final Class<?> testClazz, final String methodName)
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
    Result res = new Result();
    runner.run(res);
    return res;
  }

  class Result extends RunNotifier {
    Failure failure;

    @Override
    public void fireTestFailure(Failure failure) {
      this.failure = failure;
    };

    boolean isOK() {
      return failure == null;
    }

    public Failure getFailure() {
      return failure;
    }
  }
}
