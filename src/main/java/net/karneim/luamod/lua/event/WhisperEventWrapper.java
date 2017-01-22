package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.DynamicTable;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(WhisperEvent event) {
    super(event, EventType.WHISPER.name());
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.sender);
    builder.add("message", delegate.message);
  }
}
