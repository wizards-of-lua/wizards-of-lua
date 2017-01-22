package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.minecraftforge.event.ServerChatEvent;

public class ServerChatEventWrapper extends EventWrapper<ServerChatEvent> {
  public ServerChatEventWrapper(@Nullable ServerChatEvent delegate) {
    super(delegate, EventType.CHAT.name());
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.getUsername());
    builder.add("message", delegate.getMessage());
  }
}
