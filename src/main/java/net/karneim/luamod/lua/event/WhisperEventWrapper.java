package net.karneim.luamod.lua.event;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class WhisperEventWrapper extends EventWrapper<String> {
  public WhisperEventWrapper(String message) {
    super(message, EventType.WHISPER_EVENT);
  }

  @Override
  protected Table toLuaObject() {
    Table result = new DefaultTable();
    result.rawset("message", delegate);
    return result;
  }
}
