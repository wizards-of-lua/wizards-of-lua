package net.karneim.luamod.lua.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;

public class EntityWrapperFactory {
  public EntityWrapper<?> create(Table env, Entity entity) {
    if (entity instanceof EntityLiving) {
      return new EntityLivingWrapper(env, (EntityLiving) entity);
    }
    if (entity instanceof EntityPlayer) {
      return new EntityPlayerWrapper(env, (EntityPlayer) entity);
    }
    return new EntityWrapper<Entity>(env, entity);
  }
}
