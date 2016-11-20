package net.karneim.luamod;

import net.karneim.luamod.lua.LuaProcessEntity;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
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
    // System.out.println("onChat: " + evt.getMessage());
    for (LuaProcessEntity p : mod.getProcessRegistry().getAll()) {
      p.onChatMessage(evt.getPlayer(), evt.getMessage());
    }
  }
}
