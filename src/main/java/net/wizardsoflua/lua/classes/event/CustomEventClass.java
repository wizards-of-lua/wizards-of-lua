package net.wizardsoflua.lua.classes.event;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

@AutoService(DeclaredLuaClass.class)
@DeclareLuaClass(name = CustomEventClass.NAME, superClass = EventClass.class)
public class CustomEventClass
    extends DelegatorLuaClass<CustomLuaEvent, CustomEventClass.Proxy<CustomLuaEvent>> {
  public static final String NAME = "CustomEvent";

  @Override
  public Proxy<CustomLuaEvent> toLua(CustomLuaEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends CustomLuaEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addReadOnly("data", this::getData);
    }

    @Override
    public String getName() {
      return delegate.getName();
    }

    private @Nullable Object getData() {
      return delegate.getData(classLoader.getViewFactory());
    }
  }
}
