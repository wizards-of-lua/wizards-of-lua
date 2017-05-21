package net.wizardsoflua.testenv;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import net.minecraft.launchwrapper.Launch;

public class MinecraftJUnitRunner extends BlockJUnit4ClassRunner {

  private static volatile CountDownLatch startSignal = new CountDownLatch(1);

  public MinecraftJUnitRunner(Class<?> klass) throws InitializationError {
    super(klass);
    String userDir = System.getProperty("user.dir");
    if (!userDir.endsWith("run")) {
      throw new IllegalStateException(
          "user.dir must be 'run' directory but was '" + userDir + "'!");
    }
  }

  @Override
  protected Statement methodBlock(FrameworkMethod method) {
    Statement next = super.methodBlock(method);
    return new WolTestStatement(next, method);
  }

  private class WolTestStatement extends Statement {
    private final FrameworkMethod method;

    public WolTestStatement(Statement next, FrameworkMethod method) {
      super();
      this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {
      startGame();
      waitForGameHasBeenStarted();
      // Make sure to get the "right" WolTestEnvironment (that has been loaded by Minecraft's
      // classloader)
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

  private static void startGame() {
    try {
      MinecraftStarter.start();
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private static void waitForGameHasBeenStarted() throws InterruptedException {
    startSignal.await(30, TimeUnit.SECONDS);
  }

  @CalledByReflection("Called by WolTestEnvironment")
  public static void onGameStarted() {
    startSignal.countDown();
  }

}
