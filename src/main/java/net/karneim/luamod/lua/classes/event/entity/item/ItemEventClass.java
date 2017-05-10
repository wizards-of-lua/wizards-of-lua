package net.karneim.luamod.lua.classes.event.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.sandius.rembulan.Table;

@LuaModule("ItemEvent")
public class ItemEventClass extends DelegatingLuaClass<ItemEvent> {
  public ItemEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends ItemEvent> b, ItemEvent delegate) {
    b.addReadOnly("entityItem", () -> repo.wrap(delegate.getEntityItem()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
