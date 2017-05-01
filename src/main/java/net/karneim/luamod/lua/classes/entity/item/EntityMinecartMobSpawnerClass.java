package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartMobSpawner")
public class EntityMinecartMobSpawnerClass extends DelegatingLuaClass<EntityMinecartMobSpawner> {
  public EntityMinecartMobSpawnerClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartMobSpawner> b,
      EntityMinecartMobSpawner d) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
