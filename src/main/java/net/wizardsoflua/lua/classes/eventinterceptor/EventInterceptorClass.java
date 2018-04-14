package net.wizardsoflua.lua.classes.eventinterceptor;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;
import net.wizardsoflua.lua.function.NamedFunction1;

@DeclareLuaClass(name = EventInterceptorClass.NAME)
public class EventInterceptorClass
    extends ProxyingLuaClass<EventInterceptor, EventInterceptorClass.Proxy<EventInterceptor>> {
  public static final String NAME = "EventInterceptor";

  public EventInterceptorClass() {
    add(new StopFunction());
  }

  @Override
  public Proxy<EventInterceptor> toLua(EventInterceptor javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends EventInterceptor> extends LuaInstance<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
    }

    @Override
    public boolean isTransferable() {
      return false;
    }
  }

  private class StopFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "stop";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      EventInterceptor self =
          getConverters().toJava(EventInterceptor.class, arg1, 1, "self", getName());
      self.stop();
      context.getReturnBuffer().setTo();
    }
  }
}
