package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLiving;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = MobClass.NAME, superClass = EntityClass.class)
public class MobClass extends ProxyCachingLuaClass<EntityLiving, MobClass.Proxy<EntityLiving>> {
  public static final String NAME = "Mob";

  @Override
  public MobClass.Proxy<EntityLiving> toLua(EntityLiving delegate) {
    return new Proxy<>(this, delegate);
  }

  public static class Proxy<D extends EntityLiving> extends EntityClass.EntityLivingBaseProxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      add("ai", this::getAi, this::setAi);
    }

    public boolean getAi() {
      return !delegate.isAIDisabled();
    }

    public void setAi(Object luaObj) {
      boolean enabled = getConverters().toJava(Boolean.class, luaObj, "ai");
      delegate.setNoAI(!enabled);
    }
  }
}
