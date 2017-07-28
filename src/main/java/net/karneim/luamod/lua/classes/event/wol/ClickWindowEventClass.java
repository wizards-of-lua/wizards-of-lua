package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("ClickWindowEvent")
public class ClickWindowEventClass extends DelegatingLuaClass<ClickWindowEvent> {
  public ClickWindowEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends ClickWindowEvent> b,
      ClickWindowEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("clickedItem", () -> repo.wrap(delegate.getClickedItem()));
    b.addReadOnly("clickType", () -> repo.wrap(delegate.getClickType()));
    b.addReadOnly("slotId", () -> repo.wrap(delegate.getSlotId()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
