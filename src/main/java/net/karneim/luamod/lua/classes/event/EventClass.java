package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;

@LuaModule("Event")
public class EventClass extends ImmutableLuaClass<Event> {
  private long id;

  public EventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, Event event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("id", repo.wrap(id++));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
