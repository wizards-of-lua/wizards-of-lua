package net.wizardsoflua.testenv;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.wizardsoflua.testenv.player.PlayerBackdoor;

public class MinecraftBackdoor {

  private final WolTestEnvironment testEnv;
  private final EventBus eventBus;
  private final PlayerBackdoor player;

  public MinecraftBackdoor(WolTestEnvironment testEnv, EventBus eventBus) {
    this.testEnv = testEnv;
    this.eventBus = eventBus;
    this.player = new PlayerBackdoor(testEnv);
  }

  public String getName() {
    return testEnv.getServer().getName();
  }

  public void post(Event event) {
    eventBus.post(event);
  }

  public PlayerBackdoor player() {
    return player;
  }

  public int executeCommand(EntityPlayerMP player, String cmd, Object... args) {
    if (args != null && args.length > 0) {
      cmd = String.format(cmd, args);
    }
    return testEnv.getServer().getCommandManager().executeCommand(player, cmd);
  }

  public Iterable<String> getChatOutputOf(EntityPlayerMP player) {
    if (player == testEnv.getTestPlayer()) {
      return Collections.emptyList();
    } else {
      throw new IllegalArgumentException();
    }
  }

  public <E extends Event> E waitFor(Class<E> eventType) {
    try {
      return testEnv.getEventRecorder().waitFor(eventType);
    } catch (InterruptedException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

}
