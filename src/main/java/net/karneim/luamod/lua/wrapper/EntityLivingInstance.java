package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;

public class EntityLivingInstance<E extends EntityLiving> extends EntityLivingBaseInstance<E> {
  public EntityLivingInstance(LuaTypesRepo repo, @Nullable E delegate, Table metatable) {
    super(repo, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
  }

}
