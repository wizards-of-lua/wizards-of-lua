package net.wizardsoflua.testenv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;

public class MinecraftJUnitRunner extends BlockJUnit4ClassRunner {

  private static MinecraftJUnitRunner RUNNER;

  private CountDownLatch startSignal = new CountDownLatch(1);

  public MinecraftJUnitRunner(Class<?> klass) throws InitializationError {
    super(klass);
    synchronized (MinecraftJUnitRunner.class) {
      if (RUNNER == null) {
        RUNNER = this;
      } else {
        throw new Error(MinecraftJUnitRunner.class.getSimpleName() + " already created!");
      }
    }
    String userDir = System.getProperty("user.dir");
    if (!userDir.endsWith("run")) {
      throw new IllegalStateException(
          "user.dir must be 'run' directory but was '" + userDir + "'!");
    }
  }

  @Override
  protected Statement methodBlock(FrameworkMethod method) {
    Statement next = super.methodBlock(method);
    return new XXXStatement(next, method);
  }

  private class XXXStatement extends Statement {
    private final FrameworkMethod method;

    public XXXStatement(Statement next, FrameworkMethod method) {
      super();
      this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {
      startGame();
      waitForServerIsStarted();
      Class<?> cls = Launch.classLoader.findClass(WolTestEnvironment.class.getName());
      try {
        Method m = cls.getMethod("runTest", String.class, String.class);
        Object result = m.invoke(cls, method.getDeclaringClass().getName(), method.getName());
        if (result != null) {
          throw (Throwable) result;
        }
      } catch (Throwable e) {
        e.printStackTrace(System.out);
        throw e;
      }
    }
  }

  private void waitForServerIsStarted() throws InterruptedException {
    startSignal.await(30, TimeUnit.SECONDS);
  }

  private static void startGame() {
    try {
      MinecraftStarter.start();
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public static void serverStarted() {
    synchronized (MinecraftJUnitRunner.class) {
      if (RUNNER == null) {
        System.err
            .println(MinecraftJUnitRunner.class.getSimpleName() + " not initialized. Ignoring!");
      } else {
        RUNNER.startSignal.countDown();
      }
    }
  }

  public static MinecraftServer getServer() {
    try {
      Class<?> cls = Launch.classLoader.findClass(WolTestEnvironment.class.getName());
      Method m = cls.getMethod("getServer");
      Object obj = m.invoke(null);
      MinecraftServer result = (MinecraftServer) obj;
      return result;
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new UndeclaredThrowableException(e);
    }
  }


}
