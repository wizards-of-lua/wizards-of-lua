package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.logging.log4j.Logger;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.minecraft.server.MinecraftServer;

public class TestClassExecutor {

  private final Logger logger;
  private final MinecraftServer server;
  private final String playerName;

  public TestClassExecutor(Logger logger, MinecraftServer server, String playerName) {
    this.logger = checkNotNull(logger, "logger==null!");
    this.server = server;
    this.playerName = playerName;
  }

  public TestResults runTests(final Class<?> testClazz) throws InitializationError {
    BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClazz);
    TestResults res = new TestResults(logger, server, playerName);
    runner.run(res);
    return res;
  }

}
