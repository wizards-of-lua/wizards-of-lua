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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * An <span class="notranslate">EventInterceptor</span> represents an interceptor of events.
 *
 * An interceptor can be obtained through [Events.intercept()](/modules/Events#intercept) and
 * [Events.on(...):call()](/modules/Events#on).
 *
 * In contrast to an [EventQueue](/modules/EventQueue), an event interceptor is capable of event
 * mutation and event cancellation since it is called 'in-line' with the event occurrence.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EventInterceptorClass.NAME)
@GenerateLuaClassTable(instance = EventInterceptorClass.Instance.class)
@GenerateLuaDoc(subtitle = "Intercepting Events")
public final class EventInterceptorClass
    extends BasicLuaClass<EventInterceptor, EventInterceptorClass.Instance<EventInterceptor>> {
  public static final String NAME = "EventInterceptor";
  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new EventInterceptorClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EventInterceptor>> toLuaInstance(EventInterceptor javaInstance) {
    return new EventInterceptorClassInstanceTable<>(new Instance<>(javaInstance), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EventInterceptor> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * The 'stop' function terminates this interceptor so that the corresponding function is no
     * longer called for new events.
     *
     * #### Example
     *
     * Intercepting the next chat event and stopping immediately once the first one occurs.
     *
     * <code>
     * local i
     * i = Events.on('ChatEvent'):call(function(event)
     *   print(event.player.name, event.message)
     *   i:unsubscribe()
     * end)
     * </code>
     */
    @LuaFunction
    public void stop() {
      delegate.stop();
    }
  }
}
