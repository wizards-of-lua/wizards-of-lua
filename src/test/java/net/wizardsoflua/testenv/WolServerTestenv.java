package net.wizardsoflua.testenv;

import static java.util.Objects.requireNonNull;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

/**
 * The {@link WolServerTestenv} is the server specific test environment. It is instantiated once per
 * {@link MinecraftServer}.
 *
 * @author Adrodoc
 */
public class WolServerTestenv {
  private static final InheritableThreadLocal<WolServerTestenv> INSTANCE =
      new InheritableThreadLocal<>();

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

  public void runOnMainThreadAndWait(Runnable runnable) {
    ListenableFuture<Object> future = server.addScheduledTask(runnable);
    try {
      future.get(30, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  public <V> V callOnMainThreadAndWait(Callable<V> callable) {
    ListenableFuture<V> future = server.callFromMainThread(callable);
    try {
      return future.get(30, TimeUnit.SECONDS);
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
