package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

@DeclareLuaClass(name = EventClass.METATABLE_NAME)
public class EventClass extends LuaClass<Event> {
  public static final String METATABLE_NAME = "Event";

  public EventClass() {
    super(Event.class);
  }

  @Override
  public Table toLua(Event javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, METATABLE_NAME);
  }

  @Override
  public Event toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends DelegatingProxy {

    private final Event delegate;
    private final String name;

    public Proxy(Converters converters, Table metatable, Event delegate, String name) {
      super(converters, metatable, delegate);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      this.name = checkNotNull(name, "name==null!");
      addImmutable("name", name);
      // addReadOnly("cancelable", () -> delegate.isCancelable());
      // addReadOnly("canceled", () -> delegate.isCanceled());
    }
    
    @Override
    public boolean isTransferable() {
      return true;
    }

    public final String getName() {
      return name;
    }
  }

}
