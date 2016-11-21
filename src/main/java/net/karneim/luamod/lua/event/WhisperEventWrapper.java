package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public class WhisperEventWrapper extends EventWrapper<String> {
  public WhisperEventWrapper(@Nullable String message) {
    super(message, EventType.WHISPER_EVENT);
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("message", delegate);
    return result;
  }
}
