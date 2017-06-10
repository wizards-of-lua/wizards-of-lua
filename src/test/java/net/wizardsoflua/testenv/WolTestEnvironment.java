package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.InitializationError;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.junit.TestClassExecutor;
import net.wizardsoflua.testenv.junit.TestMethodExecutor;
import net.wizardsoflua.testenv.junit.TestResult;
import net.wizardsoflua.testenv.junit.TestResults;
import net.wizardsoflua.testenv.net.ChatAction;
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
  public final PacketPipeline packetPipeline = new PacketPipeline(logger, CHANNEL_NAME);

  private final List<Event> events = new ArrayList<>();
  private @Nullable EntityPlayerMP testPlayer;

  private Object eventsSync = new Object();
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

  @EventHandler
  public void init(FMLInitializationEvent event) {
    packetPipeline.initialize();
    packetPipeline.registerPacket(ConfigMessage.class);
    packetPipeline.registerPacket(ChatAction.class);
    // FIXME only do this on server side (or single player?)!
    MinecraftForge.EVENT_BUS.register(this);
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

  private void addEvent(Event evt) {
    synchronized (eventsSync) {
      events.add(evt);
      eventsSync.notify();
    }
  }

  public void reset() {
    events.clear();
    // TODO other stuff to reset?
  }

//  // TODO remove this
//  public <E extends Event> Iterable<E> getEvents(Class<E> type) {
//    Predicate<Event> filter = type::isInstance;
//    Function<Event, E> cast = type::cast;
//    return Iterables.transform(Iterables.filter(events, filter), cast);
//  }

  /**
   * Blocks until an event of the specified type is received, and returns it.
   * Removes the returned event and any event that has occured before it.
   * @param eventType
   * @return the first event of the specified type
   * @throws InterruptedException
   */
  public <E extends Event> E waitFor(Class<E> eventType) throws InterruptedException {
    while (true) {
      synchronized (eventsSync) {
        while ( events.isEmpty()) {
          eventsSync.wait();
        }
        Iterator<Event> it = events.iterator();
        while (it.hasNext()) {
          Event event = it.next();
          it.remove();
          if (eventType.isInstance(event)) {
            return eventType.cast(event);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onEvent(ServerChatEvent evt) {
    System.out.println("ServerChatEvent " + evt);
    addEvent(evt);
  }

  @SubscribeEvent
  public void onEvent(RightClickBlock evt) {
    System.out.println("RightClickBlock " + evt);
    if (evt.getWorld().isRemote) {
      return;
    }
    addEvent(evt);
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
    addEvent(evt);
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
    addEvent(evt);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onDimensionLoad(WorldEvent.Load evt) {
    addEvent(evt);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onDimensionUnload(WorldEvent.Unload evt) {
    addEvent(evt);
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
