package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = LivingEventClass.NAME, superClass = EventClass.class)
public class LivingEventClass
    extends ProxyingLuaClass<LivingEvent, LivingEventClass.Proxy<LivingEvent>> {
  public static final String NAME = "LivingEvent";

  @Override
  public Proxy<LivingEvent> toLua(LivingEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends LivingEvent> extends EventClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addReadOnly("entity", this::getEntity);
    }

    protected Object getEntity() {
      EntityLivingBase entity = delegate.getEntityLiving();
      return getConverters().toLua(entity);
    }
  }
}
