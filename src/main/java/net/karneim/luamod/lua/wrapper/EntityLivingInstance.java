package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;

public class EntityLivingInstance<E extends EntityLiving> extends EntityLivingBaseInstance<E> {
  public EntityLivingInstance(Table env, @Nullable E delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
  }

}
