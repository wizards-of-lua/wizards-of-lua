package net.wizardsoflua.lua.classes.event;

import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = SwingArmEventClass.NAME, superClass = EventClass.class)
public class SwingArmEventClass
    extends ProxyingLuaClass<SwingArmEvent, SwingArmEventClass.Proxy<SwingArmEvent>> {
  public static final String NAME = "SwingArmEvent";

  @Override
  public Proxy<SwingArmEvent> toLua(SwingArmEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends SwingArmEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<D, ? extends Proxy<D>> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("hand", getConverter().toLua(delegate.getHand()));
      addImmutable("item", getConverter().toLua(delegate.getItemStack()));
      addImmutable("player", getConverter().toLua(delegate.getPlayer()));
    }
  }
}
