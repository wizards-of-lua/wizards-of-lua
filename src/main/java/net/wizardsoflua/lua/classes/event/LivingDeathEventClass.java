package net.wizardsoflua.lua.classes.event;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = LivingDeathEventClass.NAME, superClass = LivingEventClass.class)
public class LivingDeathEventClass
    extends DelegatorLuaClass<LivingDeathEvent, LivingDeathEventClass.Proxy<LivingDeathEvent>> {
  public static final String NAME = "LivingDeathEvent";

  @Override
  public Proxy<LivingDeathEvent> toLua(LivingDeathEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends LivingDeathEvent>
      extends LivingEventClass.Proxy<LivingEventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new LivingEventApi<>(luaClass, delegate));
      addReadOnly("cause", this::getCause);
    }

    protected Object getCause() {
      DamageSource source = delegate.getSource();
      return getConverters().toLua(source.damageType);
    }
  }
}
