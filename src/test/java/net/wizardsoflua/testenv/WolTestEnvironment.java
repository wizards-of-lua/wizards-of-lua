package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.junit.TestClassExecutor;
import net.wizardsoflua.testenv.junit.TestMethodExecutor;
import net.wizardsoflua.testenv.junit.TestResult;
import net.wizardsoflua.testenv.junit.TestResults;
import net.wizardsoflua.testenv.net.AbstractPacket;
import net.wizardsoflua.testenv.net.PacketPipeline;

@Mod(modid = WolTestEnvironment.MODID, version = WolTestEnvironment.VERSION,
    acceptableRemoteVersions = "*")
public class WolTestEnvironment {
  public static final String MODID = "wol-testenv";
  public static final String VERSION = WizardsOfLua.VERSION;
  private static final String CHANNEL_NAME = MODID;

  @Instance(MODID)
  public static WolTestEnvironment instance;
  @SidedProxy(clientSide = "net.wizardsoflua.testenv.client.ClientProxy",
      serverSide = "net.wizardsoflua.testenv.server.ServerProxy")
  public static CommonProxy proxy;

  public final Logger logger = LogManager.getLogger(WolTestEnvironment.class.getName());
  private final PacketPipeline packetPipeline = new PacketPipeline(logger, CHANNEL_NAME);
  private final EventRecorder eventRecorder = new EventRecorder();

  private @Nullable EntityPlayerMP testPlayer;


  private MinecraftServer server;

  public WolTestEnvironment() {

  }

  public MinecraftServer getServer() {
    return server;
  }

  public PacketPipeline getPacketPipeline() {
    return packetPipeline;
  }

  public EntityPlayerMP getTestPlayer() {
    return testPlayer;
  }

  public void setTestPlayer(EntityPlayerMP player) {
    testPlayer = player;
  }

  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    packetPipeline.initialize();
    Iterable<Class<? extends AbstractPacket>> packetClasses = findPacketClasses();
    for (Class<? extends AbstractPacket> cls : packetClasses) {
      packetPipeline.registerPacket(cls);
    }
    proxy.onInit(event);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) throws IOException {
    packetPipeline.postInitialize();
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    server = checkNotNull(event.getServer());
    event.registerServerCommand(new TestCommand());
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) throws Throwable {
    // Make sure to inform the "right" MinecraftJUnitRunner, that has been loaded
    // by the system classloader.
    Class<?> cls =
        ClassLoader.getSystemClassLoader().loadClass(MinecraftJUnitRunner.class.getName());
    Method m = cls.getMethod("onGameStarted");
    m.invoke(null);
  }

  public void beforeTest() {
    eventRecorder.clear();
    eventRecorder.setEnabled(true);
    // TODO other stuff to reset?
  }
  
  public void afterTest() {
    eventRecorder.setEnabled(false);
    eventRecorder.clear();
  }

  @CalledByReflection("Called by MinecraftJUnitRunner")
  public static Throwable runTest(String classname, String methodName) {
    final TestMethodExecutor executor = new TestMethodExecutor();
    try {
      ClassLoader cl = WolTestEnvironment.class.getClassLoader();
      Class<?> testClass = cl.loadClass(classname);

      final CountDownLatch testFinished = new CountDownLatch(1);
      final AtomicReference<TestResult> resultRef = new AtomicReference<TestResult>();
      instance.getServer().addScheduledTask(new Runnable() {

        @Override
        public void run() {
          try {
            TestResult testResult = executor.runTest(testClass, methodName);
            resultRef.set(testResult);
          } catch (InitializationError e) {
            throw new UndeclaredThrowableException(e);
          }
          testFinished.countDown();
        }
      });
      testFinished.await(30, TimeUnit.SECONDS);
      TestResult testResult = resultRef.get();
      if (!testResult.isOK()) {
        return testResult.getFailure().getException();
      }
      return null;
    } catch (InterruptedException | ClassNotFoundException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public TestResult runTestMethod(Class<?> testClass, String methodName)
      throws InitializationError {
    TestMethodExecutor executor = new TestMethodExecutor();
    TestResult result = executor.runTest(testClass, methodName);
    return result;
  }

  public Iterable<TestResults> runAllTests() throws InitializationError {
    List<TestResults> result = new ArrayList<>();
    Iterable<Class<?>> testClasses = findTestClasses();
    TestClassExecutor executor = new TestClassExecutor();
    for (Class<?> testClass : testClasses) {
      result.add(executor.runTests(testClass));
    }
    return result;
  }

  public TestResults runTests(Class<?> testClass) throws InitializationError {
    TestClassExecutor executor = new TestClassExecutor();
    return executor.runTests(testClass);
  }

  @SuppressWarnings("unchecked")
  private Iterable<Class<? extends AbstractPacket>> findPacketClasses() {
    try {
      List<Class<? extends AbstractPacket>> result = new ArrayList<>();
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> xx = classpath.getTopLevelClassesRecursive("net.wizardsoflua.testenv.net");
      Iterable<ClassInfo> yy = Iterables.filter(xx, input -> {
        return AbstractPacket.class.isAssignableFrom(input.load());
      });
      for (ClassInfo classInfo : yy) {
        result.add((Class<? extends AbstractPacket>) classInfo.load());
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

  
}
