package net.wizardsoflua.testenv;

import java.util.Collections;
import java.util.Iterator;

import org.assertj.core.internal.cglib.proxy.UndeclaredThrowableException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
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

  public Iterable<ServerChatEvent> chatEvents() {
    return testEnv.getEvents(ServerChatEvent.class);
  }

  public Iterable<RightClickBlock> rightClickBlockEvents() {
    return testEnv.getEvents(RightClickBlock.class);
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

  public ServerChatEvent waitForChatEvent() {
    // TODO replace busy wait with semaphore
    while (true) {
      Iterable<ServerChatEvent> events = testEnv.getEvents(ServerChatEvent.class);
      Iterator<ServerChatEvent> iterator = events.iterator();
      if (iterator.hasNext()) {
        ServerChatEvent evt = iterator.next();
        return evt;
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new UndeclaredThrowableException(e);
      }
    }
  }

}
