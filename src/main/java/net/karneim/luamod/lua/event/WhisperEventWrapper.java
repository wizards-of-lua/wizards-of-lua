package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.util.table.DelegatingTable;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(WhisperEvent event) {
    super(event, EventType.WHISPER.name());
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.sender);
    builder.add("message", delegate.message);
  }
}
