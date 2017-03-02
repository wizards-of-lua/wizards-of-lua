package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;


public class EntityLivingWrapper<E extends EntityLiving> extends EntityLivingBaseWrapper<E> {
  public EntityLivingWrapper(Table env, @Nullable E delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
  }

}
