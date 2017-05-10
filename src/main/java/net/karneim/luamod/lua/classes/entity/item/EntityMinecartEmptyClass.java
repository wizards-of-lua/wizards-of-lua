package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartEmpty")
public class EntityMinecartEmptyClass extends DelegatingLuaClass<EntityMinecartEmpty> {
  public EntityMinecartEmptyClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartEmpty> b,
      EntityMinecartEmpty delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
