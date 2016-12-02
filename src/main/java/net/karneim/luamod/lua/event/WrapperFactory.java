package net.karneim.luamod.lua.event;

import net.minecraftforge.event.ServerChatEvent;

public class WrapperFactory {

  public EventWrapper<ServerChatEvent> wrap(ServerChatEvent evt) {
    return new ServerChatEventWrapper(evt);
  }

}
