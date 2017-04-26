package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("WhisperEvent")
public class WhisperEventClass extends ImmutableLuaClass<WhisperEvent> {
  public WhisperEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, WhisperEvent event) {
    b.add("type", repo.wrap(getModuleName()));
    
    b.add("message", repo.wrap(event.getMessage()));
    b.add("username", repo.wrap(event.getSender()));
    b.add("player", repo.wrap(event.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
