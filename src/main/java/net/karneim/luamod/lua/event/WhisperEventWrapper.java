package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(WhisperEvent event) {
    super(event, EventType.WHISPER.name());
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    builder.add("sender", delegate.sender);
    builder.add("message", delegate.message);
  }
}
