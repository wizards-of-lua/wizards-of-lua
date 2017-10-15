package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

@DeclareLuaClass(name = SwingArmEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class SwingArmEventClass extends LuaClass<SwingArmEvent> {
  public static final String METATABLE_NAME = "SwingArmEvent";

  public SwingArmEventClass() {
    super(SwingArmEvent.class);
  }

  @Override
  public Table toLua(SwingArmEvent javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, METATABLE_NAME);
  }

  @Override
  public SwingArmEvent toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends EventClass.Proxy {

    private final SwingArmEvent delegate;

    public Proxy(Converters converters, Table metatable, SwingArmEvent delegate, String name) {
      super(converters, metatable, delegate, name);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("itemStack", getConverters().toLua(delegate.getItemStack()));
    }

  }

}
