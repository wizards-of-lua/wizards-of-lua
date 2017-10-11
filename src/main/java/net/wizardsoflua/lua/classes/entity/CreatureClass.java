package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.DeclareLuaClass;

@DeclareLuaClass(name = CreatureClass.METATABLE_NAME, superclassname = EntityClass.METATABLE_NAME)
public class CreatureClass extends InstanceCachingLuaClass<EntityLivingBase> {
  public static final String METATABLE_NAME = "Creature";

  public CreatureClass() {
    super(EntityLivingBase.class);
  }
  
  @Override
  public Table toLua(EntityLivingBase delegate) {
    return new Proxy(getConverters(), getMetatable(), delegate);
  }

  @Override
  public EntityLivingBase toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends EntityClass.Proxy {

    private final EntityLivingBase delegate;

    public Proxy(Converters converters, Table metatable, EntityLivingBase delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
    }

    @Override
    public float getRotationYaw() {
      float v = delegate.renderYawOffset;
      return MathHelper.wrapDegrees(v);
    }
  }

}
