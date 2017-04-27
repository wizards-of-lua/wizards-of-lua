package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("ClickWindowEvent")
public class ClickWindowEventClass extends DelegatingLuaClass<ClickWindowEvent> {
  public ClickWindowEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, ClickWindowEvent event) {
    b.add("clickedItem", repo.wrap(event.getClickedItem()));
    b.add("clickType", repo.wrap(event.getClickType()));
    b.add("slotId", repo.wrap(event.getSlotId()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
