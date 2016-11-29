package net.karneim.luamod.lua.event;

import net.karneim.luamod.LuaMod;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class ModEventHandler {
  private LuaMod mod;

  public ModEventHandler(LuaMod mod) {
    this.mod = mod;
  }

  @SubscribeEvent
  public void onCommand(CommandEvent evt) {
    // System.out.println("onCommand: " + evt.getCommand().getCommandName() + " "
    // + Arrays.toString(evt.getParameters()));
  }

  @SubscribeEvent
  public void onChat(ServerChatEvent evt) {
    mod.notifyEventListeners(new ServerChatEventWrapper(evt));
  }

  @SubscribeEvent
  public void onLeftClickBlock(LeftClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    mod.notifyEventListeners(
        new PlayerInteractEventWrapper<LeftClickBlock>(evt, EventType.LEFT_CLICK));
  }

  @SubscribeEvent
  public void onRightClickBlock(RightClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    mod.notifyEventListeners(
        new PlayerInteractEventWrapper<RightClickBlock>(evt, EventType.RIGHT_CLICK));
  }

  // @SubscribeEvent
  // public void onRightClickItem(RightClickItem evt) {
  // System.out.println("RightClickItem");
  // mod.notifyEventListeners(
  // new PlayerInteractEventWrapper<RightClickItem>(evt, EventType.RIGHT_CLICK));
  // }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    mod.notifyEventListeners(
        new Player2EventWrapper<PlayerLoggedInEvent>(evt, EventType.PLAYER_JOINED));
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
    mod.notifyEventListeners(
        new Player2EventWrapper<PlayerLoggedOutEvent>(evt, EventType.PLAYER_LEFT));
  }

  @SubscribeEvent
  public void onPlayerRespawn(PlayerRespawnEvent evt) {
    mod.notifyEventListeners(
        new Player2EventWrapper<PlayerRespawnEvent>(evt, EventType.PLAYER_SPAWNED));
  }

}
