package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;

@LuaModule("Event")
public class EventClass extends DelegatingLuaClass<Event> {
  private long id;

  public EventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, Event event) {
    b.add("id", repo.wrap(id++));
    b.add("type", repo.wrap(getModuleName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
