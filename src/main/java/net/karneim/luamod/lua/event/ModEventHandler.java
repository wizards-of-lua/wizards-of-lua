package net.karneim.luamod.lua.event;

import net.karneim.luamod.LuaMod;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    mod.notifyEventListeners(new PlayerInteractEventWrapper(evt, EventType.LEFT_CLICK));
  }

  @SubscribeEvent
  public void onLeftClickEmpty(LeftClickEmpty evt) {
    mod.notifyEventListeners(new PlayerInteractEventWrapper(evt, EventType.LEFT_CLICK));
  }

}
