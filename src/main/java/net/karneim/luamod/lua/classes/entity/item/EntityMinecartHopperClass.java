package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartHopper")
public class EntityMinecartHopperClass extends DelegatingLuaClass<EntityMinecartHopper> {
  public EntityMinecartHopperClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartHopper> b,
      EntityMinecartHopper delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
