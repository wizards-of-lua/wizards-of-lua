package net.wizardsoflua.testenv.junit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class TestClassExecutor {

  public TestResults runTests(final Class<?> testClazz) throws InitializationError {
    BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClazz);
    TestResults res = new TestResults();
    runner.run(res);
    return res;
  }

}
