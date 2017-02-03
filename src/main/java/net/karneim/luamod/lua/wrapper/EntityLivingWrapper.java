package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLiving;


public class EntityLivingWrapper<E extends EntityLiving> extends EntityLivingBaseWrapper<E> {
  public EntityLivingWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
  }

}
