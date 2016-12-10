package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;

public class ServerChatEventWrapper extends EventWrapper<ServerChatEvent> {
  public ServerChatEventWrapper(@Nullable ServerChatEvent delegate) {
    super(delegate, EventType.CHAT.name());
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("sender", delegate.getUsername());
    result.rawset("message", delegate.getMessage());
    return result;
  }
}
