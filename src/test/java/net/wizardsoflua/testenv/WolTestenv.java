package net.wizardsoflua.testenv;

import static java.util.Objects.requireNonNull;
import java.nio.file.FileSystem;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.wizardsoflua.TimeService;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.filesystem.WolServerFileSystem;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.spell.SpellRegistry;
import net.wizardsoflua.testenv.junit.AbortExtension;
import net.wizardsoflua.testenv.net.NetworkMessage;
import net.wizardsoflua.testenv.net.WolTestPacketChannel;

/**
 * The {@link WolTestenv} is the test run specific test environment. It should be instantiated once
 * per test run. When instantiating this class the instance is stored in a {@link ThreadLocal}
 * variable and can be accessed via {@link #getInstanceForCurrentThread()}. To allow for garbage
 * collection when used in long running threads and to prevent
 * {@link #getInstanceForCurrentThread()} to return {@link #close() closed} instances it is
 * recommended to {@link #close()} {@link WolTestenv}s in the same thread they were created.
 * <p>
 * Usage example:
 *
 * <code><pre>
 * try (WolTestenv testenv = new WolTestenv(mod, server, player)) {
 *   // run tests
 * }
 * </pre></code>
 *
 * @author Adrodoc
 */
public final class WolTestenv implements AutoCloseable {
  private static final ThreadLocal<WolTestenv> INSTANCE = new ThreadLocal<>();

  /**
   * Returns the {@link WolTestenv} instance associated with the current thread. A
   * {@link WolTestenv} is associated with the thread that created it until it is {@link #close()
   * closed}.
   * <p>
   * This method is used by JUnit Tests to find the correct instance, because there is no way to
   * pass a reference via the JUnit platform API to test classes.
   *
   * @return the {@link WolTestenv} instance associated with the current thread
   * @throws IllegalThreadStateException
   */
  public static WolTestenv getInstanceForCurrentThread() throws IllegalThreadStateException {
    WolTestenv result = INSTANCE.get();
    if (result != null) {
      return result;
    } else {
      throw new IllegalThreadStateException(
          "The current thread is not associated with a " + WolTestenv.class.getName());
    }
  }

  private final AbortExtension abortExtension = new AbortExtension();
  private final WolTestMod mod;
  private final InjectionScope serverScope;
  private final AtomicReference<EntityPlayerMP> testPlayer = new AtomicReference<>();

  /**
   * Creates a new {@link WolTestenv} and associates it with the current thread.
   *
   * @param mod
   * @param serverScope
   * @param testPlayer
   * @see WolTestenv
   * @see #getInstanceForCurrentThread()
   */
  public WolTestenv(WolTestMod mod, InjectionScope serverScope, EntityPlayerMP testPlayer) {
    this.mod = requireNonNull(mod, "mod");
    this.serverScope = requireNonNull(serverScope, "serverScope");
    this.testPlayer.set(requireNonNull(testPlayer, "testPlayer"));
    MinecraftForge.EVENT_BUS.register(this);
    INSTANCE.set(this);
  }

  @Override
  public void close() {
    if (INSTANCE.get() == this) {
      INSTANCE.set(null);
    }
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  public AbortExtension getAbortExtension() {
    return abortExtension;
  }

  public EventRecorder getEventRecorder() {
    return mod.getEventRecorder();
  }

  public MinecraftServer getServer() {
    return serverScope.getResource(MinecraftServer.class);
  }

  public WolConfig getConfig() {
    return serverScope.getInstance(WolConfig.class);
  }

  public LuaFunctionBinaryCache getLuaFunctionBinaryCache() {
    return serverScope.getInstance(LuaFunctionBinaryCache.class);
  }

  public SpellRegistry getSpellRegistry() {
    return serverScope.getInstance(SpellRegistry.class);
  }

  public TimeService getTimeService() {
    return serverScope.getInstance(TimeService.class);
  }

  public FileSystem getWorldFileSystem() {
    return serverScope.getInstance(WolServerFileSystem.class);
  }

  @SubscribeEvent
  public void onEvent(PlayerEvent.Clone evt) {
    EntityPlayer oldPlayer = evt.getOriginal();
    EntityPlayer newPlayer = evt.getEntityPlayer();
    if (oldPlayer instanceof EntityPlayerMP && newPlayer instanceof EntityPlayerMP) {
      testPlayer.compareAndSet((EntityPlayerMP) oldPlayer, (EntityPlayerMP) newPlayer);
    }
  }

  public EntityPlayerMP getTestPlayer() {
    return testPlayer.get();
  }

  private static final int SYNCED = -1;
  /**
   * The amount of changes to be run on the server thread. A value of {@value #SYNCED} indicates,
   * that not only are there no pending changes, but all finished changes were synced to the client.
   */
  private final AtomicInteger pendingChangeCount = new AtomicInteger();
  private final Object changeLock = new Object();
  private final AtomicInteger pendingTaskCount = new AtomicInteger();
  private final Object taskLock = new Object();

  @SubscribeEvent
  public void onTick(ServerTickEvent event) {
    if (event.phase == Phase.START) {
      if (pendingChangeCount.compareAndSet(0, SYNCED)) {
        synchronized (changeLock) {
          changeLock.notifyAll();
        }
      }
    }
  }

  /**
   * Waits until previous changes made to the world via {@link #runChangeOnMainThread(Runnable)}
   * were synced to the client first.
   * <p>
   * Tasks registered via {@link #runChangeOnMainThread(Runnable)} will run in the beginning of a
   * server tick, before stuff like the {@link PlayerChunkMap} is ticked which causes changes to be
   * send to the client. So this method blocks until there are no more pending tasks and the next
   * server tick has ended. We wait for the beginning of the next server tick to ensure that the
   * network has enough time to propagate the changes to the client.
   */
  public void waitForSyncedClient() {
    synchronized (changeLock) {
      while (pendingChangeCount.get() != SYNCED) {
        try {
          changeLock.wait();
        } catch (InterruptedException ignore) {
        }
      }
    }
  }

  /**
   * Waits until all actions submitted via {@link #callOnMainThread(Callable)},
   * {@link #runOnMainThread(Runnable)} or {@link #runChangeOnMainThread(Runnable)} are executed.
   */
  public void waitForPendingActions() {
    synchronized (taskLock) {
      while (pendingTaskCount.get() != 0) {
        try {
          taskLock.wait();
        } catch (InterruptedException ignore) {
        }
      }
    }
  }

  private void whenTaskSubmitted() {
    pendingTaskCount.incrementAndGet();
  }

  private void whenTaskFinished() {
    if (pendingTaskCount.decrementAndGet() == 0) {
      synchronized (taskLock) {
        taskLock.notifyAll();
      }
    }
  }

  /**
   * Runs the specified {@link Runnable} on the main server thread and causes future calls to
   * {@link #sendTo(EntityPlayerMP, NetworkMessage)} to block until the changes made by the
   * specified {@link Runnable} were synced to the client.
   *
   * @param task the {@link Runnable} to run on the main server thread
   * @return a future representing pending completion of the task
   */
  public ListenableFuture<Object> runChangeOnMainThread(Runnable task) {
    pendingChangeCount.updateAndGet(it -> it == SYNCED ? 1 : it + 1);
    return runOnMainThread(() -> {
      try {
        task.run();
      } finally {
        pendingChangeCount.decrementAndGet();
      }
    });
  }

  /**
   * Runs the specified {@link Runnable} on the main server thread. If you intend to make changes to
   * the world consider using {@link #runChangeOnMainThread(Runnable)}.
   *
   * @param task the {@link Runnable} to run on the main server thread
   * @return a future representing pending completion of the task
   */
  public ListenableFuture<Object> runOnMainThread(Runnable task) {
    whenTaskSubmitted();
    return getServer().addScheduledTask(() -> {
      try {
        task.run();
      } finally {
        whenTaskFinished();
      }
    });
  }

  public <V> V callOnMainThread(Callable<V> task) {
    whenTaskSubmitted();
    ListenableFuture<V> future = getServer().callFromMainThread(() -> {
      try {
        return task.call();
      } finally {
        whenTaskFinished();
      }
    });
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sends the specified {@link NetworkMessage} to the specified {@link EntityPlayerMP}, blocking
   * until previous changes made to the world via {@link #runChangeOnMainThread(Runnable)} were
   * synced to the client first.
   *
   * @param player
   * @param message
   * @see #waitForSyncedClient()
   */
  public void sendTo(EntityPlayerMP player, NetworkMessage message) {
    waitForSyncedClient();
    WolTestPacketChannel.sendTo(player, message);
  }
}
