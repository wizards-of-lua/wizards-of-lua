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
  protected void addProperties(ImmutableTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.getUsername());
    builder.add("message", delegate.getMessage());
  }
}
