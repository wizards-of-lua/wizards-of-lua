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
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.junit.TestClassExecutor;
import net.wizardsoflua.testenv.junit.TestMethodExecutor;
import net.wizardsoflua.testenv.junit.TestResult;
import net.wizardsoflua.testenv.junit.TestResults;
import net.wizardsoflua.testenv.net.ChatAction;
import net.wizardsoflua.testenv.net.ClickAction;
import net.wizardsoflua.testenv.net.ConfigMessage;
import net.wizardsoflua.testenv.net.PacketPipeline;

@Mod(modid = WolTestEnvironment.MODID, version = WolTestEnvironment.VERSION,
    acceptableRemoteVersions = "*")
public class WolTestEnvironment {
  public static final String MODID = "wol-test";
  public static final String VERSION = WizardsOfLua.VERSION;
  private static final String CHANNEL_NAME = MODID;

  @Instance(MODID)
  public static WolTestEnvironment instance;

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
  
  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    packetPipeline.initialize();
    // TODO register packets automatically by scanning classes
    packetPipeline.registerPacket(ConfigMessage.class);
    packetPipeline.registerPacket(ChatAction.class);
    packetPipeline.registerPacket(ClickAction.class);
    // FIXME only do this on server side (or single player?)!
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(eventRecorder);
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

  public void reset() {
    eventRecorder.clear();
    // TODO other stuff to reset?
  }


  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent evt) {
    System.out.println("PlayerLoggedInEvent " + evt);
    System.out.println("PlayerLoggedInEvent");
    if (testPlayer == null) {
      testPlayer = (EntityPlayerMP) evt.player;
      setOperator(testPlayer);
      EntityPlayerMP player = (EntityPlayerMP) evt.player;
      ConfigMessage message = new ConfigMessage();
      message.wolVersionOnServer = VERSION;
      getPacketPipeline().sendTo(message, player);
    }
  }

  private void setOperator(EntityPlayerMP player) {
    GameProfile gameprofile =
        server.getPlayerProfileCache().getGameProfileForUsername(player.getName());
    server.getPlayerList().addOp(gameprofile);
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedOutEvent evt) {
    System.out.println("PlayerLoggedOutEvent evt");
    if (testPlayer != null && testPlayer == evt.player) {
      testPlayer = null;
    }
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

  private Iterable<Class<?>> findTestClasses() {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> xx = classpath.getTopLevelClassesRecursive("net.wizardsoflua");
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
