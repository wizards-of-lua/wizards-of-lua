package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = LivingEventClass.NAME, superClass = EventClass.class)
public class LivingEventClass extends
    DelegatorLuaClass<LivingEvent, LivingEventClass.Proxy<LivingEventApi<LivingEvent>, LivingEvent>> {
  public static final String NAME = "LivingEvent";

  @Override
  public Proxy<LivingEventApi<LivingEvent>, LivingEvent> toLua(LivingEvent javaObj) {
    return new Proxy<>(new LivingEventApi<>(this, javaObj));
  }

  public static class Proxy<A extends LivingEventApi<D>, D extends LivingEvent>
      extends EventClass.Proxy<A, D> {
    public Proxy(A api) {
      super(api);
      addReadOnly("entity", this::getEntity);
    }

    protected Object getEntity() {
      EntityLivingBase entity = delegate.getEntityLiving();
      return getConverters().toLua(entity);
    }
  }
}
