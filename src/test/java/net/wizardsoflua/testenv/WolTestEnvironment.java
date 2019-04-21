package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.junit.TestClassExecutor;
import net.wizardsoflua.testenv.junit.TestMethodExecutor;
import net.wizardsoflua.testenv.junit.TestResults;
import net.wizardsoflua.testenv.net.AbstractMessage;
import net.wizardsoflua.testenv.net.PacketDispatcher;

@Mod(WolTestEnvironment.MODID)
public class WolTestEnvironment {
  public static final String MODID = "wol-testenv";
  public static final String VERSION = WizardsOfLua.VERSION;

  @Instance(MODID)
  public static WolTestEnvironment instance;
  @SidedProxy(clientSide = "net.wizardsoflua.testenv.client.ClientProxy",
      serverSide = "net.wizardsoflua.testenv.server.ServerProxy")
  public static CommonProxy proxy;

  public final Logger logger = LogManager.getLogger(WolTestEnvironment.class.getName());
  private final EventRecorder eventRecorder = new EventRecorder();
  private PacketDispatcher packetDispatcher;
  private AtomicReference<EntityPlayerMP> testPlayer = new AtomicReference<>();

  private MinecraftServer server;

  public WolTestEnvironment() {}

  public PacketDispatcher getPacketDispatcher() {
    return packetDispatcher;
  }

  public MinecraftServer getServer() {
    return server;
  }

  public WizardsOfLua getWol() {
    return WizardsOfLua.instance;
  }

  public @Nullable EntityPlayerMP getTestPlayer() {
    return testPlayer.get();
  }

  public void setTestPlayer(EntityPlayerMP player) {
    if (player != null) {
      logger.info("Setting testPlayer to Player: " + player.getName());
    } else {
      logger.info("Setting testPlayer to null");
    }
    testPlayer.set(player);
  }

  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    packetDispatcher = new PacketDispatcher(MODID, proxy);
    Iterable<Class<? extends AbstractMessage>> messageClasses = findMessageClasses();
    for (Class<? extends AbstractMessage> cls : messageClasses) {
      packetDispatcher.registerMessage(cls);
    }
    proxy.onInit(event);
  }

  @SubscribeEvent
  public void serverLoad(FMLServerStartingEvent event) {
    server = checkNotNull(event.getServer());
    CommandDispatcher<CommandSource> cmdDispatcher = event.getCommandDispatcher();
    TestCommand.register(cmdDispatcher);
  }

  @SubscribeEvent
  public void serverStarted(FMLServerStartedEvent event) throws Throwable {
    // Make sure to inform the "right" MinecraftJUnitRunner, that has been loaded
    // by the system classloader.
    Class<?> cls =
        ClassLoader.getSystemClassLoader().loadClass(MinecraftJUnitRunner.class.getName());
    Method m = cls.getMethod("onGameStarted");
    m.invoke(null);
  }

  @CalledByReflection("Called by MinecraftJUnitRunner")
  public static Throwable runTest(String classname, String methodName) {
    MinecraftServer server = null;
    String playerName = null;
    final TestMethodExecutor executor = new TestMethodExecutor(instance.logger, server, playerName);
    try {
      ClassLoader cl = WolTestEnvironment.class.getClassLoader();
      Class<?> testClass = cl.loadClass(classname);

      final CountDownLatch testFinished = new CountDownLatch(1);
      final AtomicReference<TestResults> resultRef = new AtomicReference<>();
      instance.getServer().addScheduledTask(new Runnable() {

        @Override
        public void run() {
          try {
            TestResults testResult = executor.runTest(testClass, methodName);
            resultRef.set(testResult);
          } catch (InitializationError e) {
            throw new UndeclaredThrowableException(e);
          }
          testFinished.countDown();
        }
      });
      testFinished.await(30, TimeUnit.SECONDS);
      TestResults testResult = resultRef.get();
      if (!testResult.isOK()) {
        Failure failure = testResult.getFailures().iterator().next();
        return failure.getException();
      }
      return null;
    } catch (InterruptedException | ClassNotFoundException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public TestResults runTestMethod(Class<?> testClass, String methodName)
      throws InitializationError {
    String playerName = getTestPlayer().getName();
    TestMethodExecutor executor = new TestMethodExecutor(logger, server, playerName);
    TestResults result = executor.runTest(testClass, methodName);
    return result;
  }

  public Iterable<TestResults> runAllTests() throws InitializationError {
    List<TestResults> result = new ArrayList<>();
    Iterable<Class<?>> testClasses = findTestClasses();
    String playerName = getTestPlayer().getName();
    TestClassExecutor executor = new TestClassExecutor(logger, server, playerName);
    for (Class<?> testClass : testClasses) {
      result.add(executor.runTests(testClass));
    }
    return result;
  }

  public TestResults runTests(Class<?> testClass) throws InitializationError {
    String playerName = getTestPlayer().getName();
    TestClassExecutor executor = new TestClassExecutor(logger, server, playerName);
    return executor.runTests(testClass);
  }

  @SuppressWarnings("unchecked")
  private Iterable<Class<? extends AbstractMessage>> findMessageClasses() {
    try {
      List<Class<? extends AbstractMessage>> result = new ArrayList<>();
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> xx =
          classpath.getTopLevelClassesRecursive("net.wizardsoflua.testenv.net");
      Iterable<ClassInfo> yy = Iterables.filter(xx, input -> {
        Class<?> cls = input.load();
        return AbstractMessage.class.isAssignableFrom(cls)
            && !Modifier.isAbstract(cls.getModifiers());
      });
      for (ClassInfo classInfo : yy) {
        result.add((Class<? extends AbstractMessage>) classInfo.load());
      }
      return result;
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private Iterable<Class<?>> findTestClasses() {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> xx = classpath.getTopLevelClassesRecursive("net.wizardsoflua.tests");
      Iterable<ClassInfo> yy = Iterables.filter(xx, input -> hasTestMethod(input));
      return Iterables.transform(yy, ClassInfo::load);
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private boolean hasTestMethod(ClassInfo input) {
    Class<?> cls = input.load();
    Method[] mm = cls.getDeclaredMethods();
    for (Method method : mm) {
      if (method.getAnnotation(org.junit.Test.class) != null) {
        return true;
      }
    }
    return false;
  }

  public void runAndWait(Task task) {
    MinecraftServer server = getServer();
    final CountDownLatch taskFinished = new CountDownLatch(1);
    final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
    server.addScheduledTask(new Runnable() {
      @Override
      public void run() {
        try {
          task.run();
        } catch (Throwable t) {
          exceptionRef.set(t);
        } finally {
          taskFinished.countDown();
        }
      }
    });
    try {
      taskFinished.await(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new UndeclaredThrowableException(e);
    }
    if (exceptionRef.get() != null) {
      throw new UndeclaredThrowableException(exceptionRef.get());
    }
  }


}
