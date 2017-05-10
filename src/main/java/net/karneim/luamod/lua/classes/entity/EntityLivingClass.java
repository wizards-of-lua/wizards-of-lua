package net.karneim.luamod.lua.classes.entity;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;

@LuaModule("EntityLiving")
public class EntityLivingClass extends DelegatingLuaClass<EntityLiving> {
  public EntityLivingClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends EntityLiving> b,
      EntityLiving delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
