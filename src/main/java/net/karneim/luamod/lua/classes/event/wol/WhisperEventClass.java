package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("WhisperEvent")
public class WhisperEventClass extends DelegatingLuaClass<WhisperEvent> {
  public WhisperEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, WhisperEvent event) {
    b.add("sender", repo.wrap(event.getSender()));
    b.add("message", repo.wrap(event.getMessage()));
    b.add("username", repo.wrap(event.getSender()));
    b.add("player", repo.wrap(event.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
