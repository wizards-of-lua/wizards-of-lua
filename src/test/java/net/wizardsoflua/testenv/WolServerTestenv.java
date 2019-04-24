package net.wizardsoflua.testenv;

import static java.util.Objects.requireNonNull;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.wizardsoflua.testenv.net.NetworkMessage;
import net.wizardsoflua.testenv.net.WolTestPacketChannel;

/**
 * The {@link WolServerTestenv} is the server specific test environment. It is instantiated once per
 * {@link MinecraftServer}.
 *
 * @author Adrodoc
 */
public class WolServerTestenv {
  private static final ThreadLocal<WolServerTestenv> INSTANCE = new ThreadLocal<>();

  /**
   * Returns the {@link WolServerTestenv} instance previously {@link #associateWithCurrentThread()
   * associated with the current thread}. This method is used by JUnit Tests to find the correct
   * instance, because there is no way to pass a reference to test classes via the JUnit platform
   * API.
   *
   * @return the {@link WolServerTestenv} instance previously {@link #associateWithCurrentThread()
   *         associated with the current thread}
   * @throws IllegalThreadStateException
   */
  public static WolServerTestenv getInstanceForCurrentThread() throws IllegalThreadStateException {
    WolServerTestenv result = INSTANCE.get();
    if (result != null) {
      return result;
    } else {
      throw new IllegalThreadStateException(
          "The current thread is not associated with a " + WolServerTestenv.class.getName());
    }
  }

  public void associateWithCurrentThread() {
    INSTANCE.set(this);
  }

  public static void disassociateWithCurrentThread() {
    INSTANCE.set(null);
  }

  private final WolTestenv testenv;
  private final MinecraftServer server;

  public WolServerTestenv(WolTestenv testenv, MinecraftServer server) {
    this.testenv = requireNonNull(testenv, "testenv");
    this.server = requireNonNull(server, "server");
  }

  public WolTestenv getTestenv() {
    return testenv;
  }

  public MinecraftServer getServer() {
    return server;
  }

  private static final int SYNCED = -1;
  /**
   * The amount of changes to be run on the server thread. A value of {@value #SYNCED} indicates,
   * that not only are there no pending changes, but all finished changes were synced to the client.
   */
  private final AtomicInteger pendingChangeCount = new AtomicInteger();
  private final Object lock = new Object();

  @SubscribeEvent
  public void onTick(ServerTickEvent event) {
    if (event.phase == Phase.END) {
      synchronized (lock) {
        if (pendingChangeCount.compareAndSet(0, SYNCED)) {
          lock.notifyAll();
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
   * send to the client. So this method blocks until there are no more pending tasks and the server
   * tick has ended.
   */
  public void waitForSyncedClient() {
    synchronized (lock) {
      while (pendingChangeCount.get() != SYNCED) {
        try {
          lock.wait();
        } catch (InterruptedException ignore) {
        }
      }
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
    WolTestPacketChannel channel = testenv.getPacketChannel();
    channel.sendTo(player, message);
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
    MinecraftServer server = getServer();
    pendingChangeCount.updateAndGet(it -> it == SYNCED ? 1 : it + 1);
    return server.addScheduledTask(() -> {
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
  public ListenableFuture<Object> runOnMainThread(Runnable runnable) {
    return server.addScheduledTask(runnable);
  }

  public <V> V callOnMainThread(Callable<V> callable) {
    ListenableFuture<V> future = server.callFromMainThread(callable);
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @deprecated There is no good reason to wait for the execution of the {@link Runnable} unless
   *             you need a result, in which case you should use
   *             {@link #callOnMainThread(Callable)}. In other cases use
   *             {@link #runChangeOnMainThread(Runnable)} or {@link #runOnMainThread(Runnable)}.
   *
   * @param runnable
   */
  @Deprecated
  public void runOnMainThreadAndWait(Runnable runnable) {
    ListenableFuture<Object> future = server.addScheduledTask(runnable);
    try {
      future.get(30, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  private final AtomicReference<EntityPlayerMP> testPlayer = new AtomicReference<>();

  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent evt) {
    EntityPlayerMP newPlayer = (EntityPlayerMP) evt.getPlayer();
    updateTestPlayerIfSameAs(newPlayer);
  }

  @SubscribeEvent
  public void onEvent(PlayerRespawnEvent evt) {
    EntityPlayerMP newPlayer = (EntityPlayerMP) evt.getPlayer();
    updateTestPlayerIfSameAs(newPlayer);
  }

  private void updateTestPlayerIfSameAs(EntityPlayerMP newPlayer) {
    testPlayer.updateAndGet(oldPlayer -> {
      if (oldPlayer != null && oldPlayer.getUniqueID().equals(newPlayer.getUniqueID())) {
        return newPlayer;
      } else {
        return oldPlayer;
      }
    });
  }

  public @Nullable EntityPlayerMP getTestPlayer() {
    return testPlayer.get();
  }

  public void setTestPlayer(@Nullable EntityPlayerMP testPlayer) {
    this.testPlayer.set(testPlayer);
  }
}
