package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;

public class ServerChatEventWrapper extends EventWrapper<ServerChatEvent> {
  public ServerChatEventWrapper(@Nullable ServerChatEvent delegate) {
    super(delegate, EventType.CHAT.name());
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    builder.add("sender", delegate.getUsername());
    builder.add("message", delegate.getMessage());
  }
}
