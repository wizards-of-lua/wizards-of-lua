package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.sandius.rembulan.Table;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(Table env, WhisperEvent event, Table metatable) {
    super(env, event, EventType.WHISPER.name(), metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.sender);
    builder.add("message", delegate.message);
  }
}
