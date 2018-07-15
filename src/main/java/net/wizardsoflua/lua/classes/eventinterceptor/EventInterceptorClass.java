package net.wizardsoflua.lua.classes.eventinterceptor;

import com.google.auto.service.AutoService;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EventInterceptorClass.NAME)
@GenerateLuaClassTable(instance = EventInterceptorClass.Instance.class)
@GenerateLuaDoc
public class EventInterceptorClass
    extends BasicLuaClass<EventInterceptor, EventInterceptorClass.Instance<?>> {
  public static final String NAME = "EventInterceptor";
  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new EventInterceptorClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(EventInterceptor javaInstance) {
    return new EventInterceptorClassInstanceTable<>(new Instance<>(javaInstance), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EventInterceptor> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    @LuaFunction
    public void stop() {
      delegate.stop();
    }
  }
}
