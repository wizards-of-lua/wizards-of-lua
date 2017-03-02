package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;

public class ServerChatEventWrapper extends EventWrapper<ServerChatEvent> {
  public ServerChatEventWrapper(Table env, @Nullable ServerChatEvent delegate) {
    super(env, delegate, EventType.CHAT.name());
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.getUsername());
    builder.add("message", delegate.getMessage());
  }
}
