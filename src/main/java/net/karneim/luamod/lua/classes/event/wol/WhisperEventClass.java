package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("WhisperEvent")
public class WhisperEventClass extends DelegatingLuaClass<WhisperEvent> {
  public WhisperEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends WhisperEvent> b,
      WhisperEvent delegate) {
    b.addReadOnly("sender", () -> repo.wrap(delegate.getSender()));
    b.addReadOnly("message", () -> repo.wrap(delegate.getMessage()));
    b.addReadOnly("username", () -> repo.wrap(delegate.getSender()));
    b.addReadOnly("player", () -> repo.wrap(delegate.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
