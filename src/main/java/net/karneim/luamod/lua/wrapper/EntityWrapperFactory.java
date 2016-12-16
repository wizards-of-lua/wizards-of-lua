package net.karneim.luamod.lua.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityWrapperFactory {
  public EntityWrapper<?> create(Entity entity) {
    if (entity instanceof EntityLiving) {
      return new EntityLivingWrapper((EntityLiving) entity);
    }
    if (entity instanceof EntityPlayer) {
      return new EntityPlayerWrapper((EntityPlayer) entity);
    }
    return new EntityWrapper<Entity>(entity);
  }
}
