package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartChest;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartChest")
public class EntityMinecartChestClass extends DelegatingLuaClass<EntityMinecartChest> {
  public EntityMinecartChestClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartChest> b,
      EntityMinecartChest delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
