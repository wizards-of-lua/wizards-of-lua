package net.wizardsoflua.lua.classes.event;

import net.sandius.rembulan.Table;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = SwingArmEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class SwingArmEventClass
    extends ProxyingLuaClass<SwingArmEvent, SwingArmEventClass.Proxy<SwingArmEvent>> {
  public static final String METATABLE_NAME = "SwingArmEvent";

  @Override
  public Proxy<SwingArmEvent> toLua(SwingArmEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends SwingArmEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("item", getConverters().toLua(delegate.getItemStack()));
    }
  }
}
