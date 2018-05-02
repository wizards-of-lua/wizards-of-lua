package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

@AutoService(DeclaredLuaClass.class)
@DeclareLuaClass (name = SwingArmEventClass.NAME, superClass = EventClass.class)
public class SwingArmEventClass
    extends DelegatorLuaClass<SwingArmEvent, SwingArmEventClass.Proxy<SwingArmEvent>> {
  public static final String NAME = "SwingArmEvent";

  @Override
  public Proxy<SwingArmEvent> toLua(SwingArmEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends SwingArmEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<D, ? extends Proxy<D>> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("item", getConverters().toLua(delegate.getItemStack()));
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
    }
  }
}
