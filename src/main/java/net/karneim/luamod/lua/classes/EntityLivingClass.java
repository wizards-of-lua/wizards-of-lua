package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.EntityLivingInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.entity.EntityLiving;

@LuaClass("EntityLiving")
public class EntityLivingClass extends AbstractLuaType {
  public EntityLivingInstance<EntityLiving> newInstance(EntityLiving delegate) {
    return new EntityLivingInstance<EntityLiving>(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
