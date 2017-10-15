package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

@DeclareLuaClass(name = CustomEventClass.METATABLE_NAME, superclassname = EventClass.METATABLE_NAME)
public class CustomEventClass extends LuaClass<CustomLuaEvent> {
  public static final String METATABLE_NAME = "CustomEvent";

  public CustomEventClass() {
    super(CustomLuaEvent.class);
  }

  @Override
  public Table toLua(CustomLuaEvent javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, javaObj.getName());
  }

  @Override
  public CustomLuaEvent toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends EventClass.Proxy {

    private final CustomLuaEvent delegate;

    public Proxy(Converters converters, Table metatable, CustomLuaEvent delegate, String name) {
      super(converters, metatable, delegate, name);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      Object content = delegate.getData().getContent();
      addImmutableNullable("data", getConverters().toLuaNullable(content));
    }
    
    @Override
    public CustomLuaEvent getDelegate() {
      return delegate;
    }
  }

}
