package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.classes.EntityClass;
import net.karneim.luamod.lua.classes.EntityLivingClass;
import net.karneim.luamod.lua.classes.EntityPlayerClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;

public class EntityWrapperFactory {
  
  public EntityInstance<?> create(Table env, Entity entity) {
    if (entity instanceof EntityLiving) {
      return EntityLivingClass.get().newInstance(env, (EntityLiving) entity);
    }
    if (entity instanceof EntityPlayer) {
      return EntityPlayerClass.get().newInstance(env, (EntityPlayer) entity);
    }
    return EntityClass.get().newInstance(env, entity);
  }
}
