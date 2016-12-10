package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(WhisperEvent event) {
    super(event, EventType.WHISPER.name());
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("sender", delegate.sender);
    result.rawset("message", delegate.message);
    return result;
  }
}
