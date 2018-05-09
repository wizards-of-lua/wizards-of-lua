package net.wizardsoflua.lua.classes.eventqueue;

import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.eventqueue.EventQueueClass.Instance;
import net.wizardsoflua.lua.extension.util.TypeTokenLuaConverter;

@AutoService(LuaConverter.class)
public class EventQueueConverter
    extends TypeTokenLuaConverter<EventQueue, EventQueueClassInstanceTable<?>> {
  @Inject
  private EventQueueClass luaClass;
  @Inject
  private EventQueueClassInstanceConverter instanceConverter;

  @Override
  public EventQueue getJavaInstance(EventQueueClassInstanceTable<?> luaInstance) {
    Instance<?> instance = instanceConverter.getJavaInstance(luaInstance);
    return instance.getDelegate();
  }

  @Override
  public EventQueueClassInstanceTable<?> getLuaInstance(EventQueue javaInstance) {
    Instance<?> instance = luaClass.createInstance(javaInstance);
    return instanceConverter.getLuaInstance(instance);
  }
}
