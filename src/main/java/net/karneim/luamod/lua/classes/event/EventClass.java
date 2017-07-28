package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
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
  protected void addProperties(DelegatingTable.Builder<? extends Event> b, Event delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("id", () -> repo.wrap(id++));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
