package net.karneim.luamod.lua.classes.event.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.sandius.rembulan.Table;

@LuaModule("ItemExpireEvent")
public class ItemExpireEventClass extends DelegatingLuaClass<ItemExpireEvent> {
  public ItemExpireEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends ItemExpireEvent> b,
      ItemExpireEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
