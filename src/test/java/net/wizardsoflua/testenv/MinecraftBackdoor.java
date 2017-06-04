package net.wizardsoflua.testenv;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.wizardsoflua.testenv.player.WolFakePlayer;

public class MinecraftBackdoor {

  private final WolTestEnvironment testEnv;
  private final EventBus eventBus;

  public MinecraftBackdoor(WolTestEnvironment testEnv, EventBus eventBus) {
    this.testEnv = testEnv;
    this.eventBus = eventBus;
  }

  public String getName() {
    return testEnv.getServer().getName();
  }

  public void post(Event event) {
    eventBus.post(event);
  }

  public WolFakePlayer player() {
    return testEnv.getFakePlayer();
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
    // TODO remove this. callers should call this directly on fake player
    if (player == testEnv.getFakePlayer()) {
      return testEnv.getFakePlayer().getChatOutput();
    } else {
      throw new IllegalArgumentException();
    }
  }

}
