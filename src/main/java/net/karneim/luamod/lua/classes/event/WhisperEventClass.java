package net.karneim.luamod.lua.classes.event;

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
    b.add("sender", repo.wrap(event.getSender()));
    b.add("message", repo.wrap(event.getMessage()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
