package net.karneim.luamod.lua.classes.event.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.sandius.rembulan.Table;

@LuaModule("ItemTossEvent")
public class ItemTossEventClass extends DelegatingLuaClass<ItemTossEvent> {
  public ItemTossEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends ItemTossEvent> b,
      ItemTossEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("player", () -> repo.wrap(delegate.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
