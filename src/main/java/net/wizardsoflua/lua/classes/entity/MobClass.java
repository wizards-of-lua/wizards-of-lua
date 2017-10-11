package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.DeclareLuaClass;

@DeclareLuaClass(name = MobClass.METATABLE_NAME, superclassname = CreatureClass.METATABLE_NAME)
public class MobClass extends InstanceCachingLuaClass<EntityLiving> {
  public static final String METATABLE_NAME = "Mob";

  public MobClass() {
    super(EntityLiving.class);
  }

  @Override
  public Table toLua(EntityLiving delegate) {
    return new Proxy(getConverters(), getMetatable(), delegate);
  }

  @Override
  public EntityLiving toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends CreatureClass.Proxy {

    private final EntityLiving delegate;

    public Proxy(Converters converters, Table metatable, EntityLiving delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
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
