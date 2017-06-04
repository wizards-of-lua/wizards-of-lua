package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.InitializationError;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.TestMethodExecutor.Result;
import net.wizardsoflua.testenv.player.WolFakePlayer;
import net.wizardsoflua.testenv.player.WolFakePlayerFactory;

@Mod(modid = WolTestEnvironment.MODID, version = WolTestEnvironment.VERSION,
    acceptableRemoteVersions = "*")
public class WolTestEnvironment {
  public static final String MODID = "wol-test";
  public static final String VERSION = WizardsOfLua.VERSION;

  @Instance(MODID)
  public static WolTestEnvironment instance;

  public final Logger logger = LogManager.getLogger(WolTestEnvironment.class.getName());

  private final List<Event> events = new ArrayList<>();
  private final WolFakePlayerFactory fakePlayerFactory = new WolFakePlayerFactory();

  private MinecraftServer server;

  @EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
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

  @SubscribeEvent
  public void onEvent(ServerChatEvent evt) {
    events.add(evt);
  }

  @SubscribeEvent
  public void onEvent(RightClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    events.add(evt);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onDimensionLoad(WorldEvent.Load event) {
    if (event.getWorld() instanceof WorldServer) {
      fakePlayerFactory.onLoadWorld((WorldServer) event.getWorld());
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onDimensionUnload(WorldEvent.Unload event) {
    if (event.getWorld() instanceof WorldServer) {
      fakePlayerFactory.onUnloadWorld((WorldServer) event.getWorld());
    }
  }

  public MinecraftServer getServer() {
    return server;
  }

  public void reset() {
    events.clear();
    // TODO other stuff to reset?
  }

  public <E extends Event> Iterable<E> getEvents(Class<E> type) {
    Predicate<Event> filter = type::isInstance;
    Function<Event, E> cast = type::cast;
    return Iterables.transform(Iterables.filter(events, filter), cast);
  }

  @CalledByReflection("Called by MinecraftJUnitRunner")
  public static Throwable runTest(String classname, String methodName) {
    final TestMethodExecutor executor = new TestMethodExecutor();
    try {
      ClassLoader cl = WolTestEnvironment.class.getClassLoader();
      Class<?> testClass = cl.loadClass(classname);

      final CountDownLatch testFinished = new CountDownLatch(1);
      final AtomicReference<TestMethodExecutor.Result> resultRef =
          new AtomicReference<TestMethodExecutor.Result>();
      instance.getServer().addScheduledTask(new Runnable() {

        @Override
        public void run() {
          try {
            Result testResult = executor.runTest(testClass, methodName);
            resultRef.set(testResult);
          } catch (InitializationError e) {
            throw new UndeclaredThrowableException(e);
          }
          testFinished.countDown();
        }
      });
      testFinished.await(30, TimeUnit.SECONDS);
      Result testResult = resultRef.get();
      if (!testResult.isOK()) {
        return testResult.getFailure().getException();
      }
      return null;
    } catch (InterruptedException | ClassNotFoundException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public String runTest2(Class<?> testClass, String methodName) throws InitializationError {
    TestMethodExecutor executor = new TestMethodExecutor();
    TestMethodExecutor.Result result = executor.runTest(testClass, methodName);
    if (result.isOK()) {
      return "OK";
    } else {
      return result.getFailure().getMessage();
    }
  }

  public WolFakePlayer getFakePlayer() {
    // TODO expose factory instead of delegating this method
    return fakePlayerFactory.getFakePlayer();
  }

}
