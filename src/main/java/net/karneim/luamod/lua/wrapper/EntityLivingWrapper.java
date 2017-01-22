package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.minecraft.entity.EntityLiving;


public class EntityLivingWrapper<E extends EntityLiving> extends EntityLivingBaseWrapper<E> {
  public EntityLivingWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
  }

}
