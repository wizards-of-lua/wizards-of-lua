package net.wizardsoflua.testenv;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class MinecraftServerBackdoor {

  private final MinecraftServer server;
  private final WolTestEnvironment testEnv;
  private final EventBus eventBus;

  public MinecraftServerBackdoor(MinecraftServer server, EventBus eventBus,
      WolTestEnvironment testEnv) {
    this.server = server;
    this.eventBus = eventBus;
    this.testEnv = testEnv;
  }

  public String getName() {
    return server.getName();
  }

  public void post(Event event) {
    eventBus.post(event);
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
    return server.getCommandManager().executeCommand(player, cmd);
  }

  public Iterable<String> getChatOutputOf(EntityPlayerMP player) {
    // TODO Auto-generated method stub
    return null;
  }

}
