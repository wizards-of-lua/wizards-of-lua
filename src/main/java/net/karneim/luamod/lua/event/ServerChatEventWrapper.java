package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class ServerChatEventWrapper extends EventWrapper<ServerChatEvent> {
  public ServerChatEventWrapper(@Nullable ServerChatEvent delegate) {
    super(delegate, EventType.CHAT_EVENT);
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("message", delegate.getMessage());
    return result;
  }
}
