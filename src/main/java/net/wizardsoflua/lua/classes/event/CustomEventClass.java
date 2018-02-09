package net.wizardsoflua.lua.classes.event;

import net.sandius.rembulan.Table;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = CustomEventClass.METATABLE_NAME, superclassname = EventClass.METATABLE_NAME)
public class CustomEventClass
    extends ProxyingLuaClass<CustomLuaEvent, CustomEventClass.Proxy<CustomLuaEvent>> {
  public static final String METATABLE_NAME = "CustomEvent";

  @Override
  public Proxy<CustomLuaEvent> toLua(CustomLuaEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends CustomLuaEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      Object content = delegate.getData().getContent();
      addImmutableNullable("data", getConverters().toLuaNullable(content));
    }

    @Override
    public String getName() {
      return delegate.getName();
    }
  }
}
