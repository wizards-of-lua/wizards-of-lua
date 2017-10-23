package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

@DeclareLuaClass(name = EventClass.METATABLE_NAME)
public class EventClass extends ProxyingLuaClass<Event, EventClass.Proxy<Event>> {
  public static final String METATABLE_NAME = "Event";

  @Override
  protected String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<Event> toLua(Event javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends Event> extends DelegatingProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("name", getName());
      // addReadOnly("cancelable", () -> delegate.isCancelable());
      // addReadOnly("canceled", () -> delegate.isCanceled());
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    public String getName() {
      return getConverters().getTypes().getTypename(this);
    }
  }
}
