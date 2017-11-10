package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import net.minecraft.server.MinecraftServer;

public class TestMethodExecutor {

  private final Logger logger;
  private final MinecraftServer server;
  private final String playerName;

  public TestMethodExecutor(Logger logger, MinecraftServer server, String playerName) {
    this.logger = checkNotNull(logger,"logger==null!");
    this.server = server;
    this.playerName = playerName;
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
    TestResults res = new TestResults(logger, server, playerName);
    runner.run(res);
    return res;
  }

}
