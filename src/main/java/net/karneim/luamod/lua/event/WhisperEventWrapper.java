package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.sandius.rembulan.Table;

public class WhisperEventWrapper extends EventWrapper<WhisperEvent> {
  public WhisperEventWrapper(LuaTypesRepo repo, WhisperEvent event, Table metatable) {
    super(repo, event, EventType.WHISPER.name(), metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("sender", delegate.sender);
    builder.add("message", delegate.message);
  }
}
