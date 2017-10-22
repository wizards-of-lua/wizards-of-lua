package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;

@DeclareLuaClass(name = MobClass.METATABLE_NAME, superclassname = EntityClass.METATABLE_NAME)
public class MobClass extends ProxyCachingLuaClass<EntityLiving, MobClass.Proxy<EntityLiving>> {
  public static final String METATABLE_NAME = "Mob";

  @Override
  public String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public MobClass.Proxy<EntityLiving> toLua(EntityLiving delegate) {
    return new Proxy<>(getConverters(), getMetatable(), delegate);
  }

  public static class Proxy<D extends EntityLiving> extends EntityClass.EntityLivingBaseProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      add("ai", this::getAi, this::setAi);
    }

    public boolean getAi() {
      return !delegate.isAIDisabled();
    }

    public void setAi(Object luaObj) {
      boolean enabled = getConverters().toJava(Boolean.class, luaObj);
      delegate.setNoAI(!enabled);
    }
  }
}
