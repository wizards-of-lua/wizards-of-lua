package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.impl.ImmutableTable;

public class EntityLivingWrapper<E extends EntityLiving> extends EntityLivingBaseWrapper<E> {
  public EntityLivingWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(ImmutableTable.Builder builder) {
    super.addProperties(builder);
  }

}
