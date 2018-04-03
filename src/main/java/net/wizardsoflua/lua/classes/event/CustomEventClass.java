package net.wizardsoflua.lua.classes.event;

import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = CustomEventClass.NAME, superClass = EventClass.class)
public class CustomEventClass
    extends ProxyingLuaClass<CustomLuaEvent, CustomEventClass.Proxy<CustomLuaEvent>> {
  public static final String NAME = "CustomEvent";

  @Override
  public Proxy<CustomLuaEvent> toLua(CustomLuaEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends CustomLuaEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      Object content = delegate.getData().getContent();
      addImmutableNullable("data", getConverters().toLuaNullable(content));
    }

    @Override
    public String getName() {
      return delegate.getName();
    }
  }
}
