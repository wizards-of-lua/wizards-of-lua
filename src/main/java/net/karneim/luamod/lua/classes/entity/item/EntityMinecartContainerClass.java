package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartContainer")
public class EntityMinecartContainerClass extends DelegatingLuaClass<EntityMinecartContainer> {
  public EntityMinecartContainerClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartContainer> b,
      EntityMinecartContainer delegate) {
    b.addReadOnly("items", () -> repo.wrap((IInventory) delegate));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
