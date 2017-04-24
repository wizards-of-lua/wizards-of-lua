package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("ClickWindowEvent")
public class ClickWindowEventClass extends ImmutableLuaClass<ClickWindowEvent> {
  public ClickWindowEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, ClickWindowEvent event) {
    b.add("clickedItem", repo.wrap(event.getClickedItem()));
    b.add("clickType", repo.wrap(event.getClickType()));
    b.add("slotId", repo.wrap(event.getSlotId()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
