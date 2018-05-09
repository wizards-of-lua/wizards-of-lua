package net.wizardsoflua.lua.classes.eventqueue;

import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.eventqueue.EventQueueClass.Instance;
import net.wizardsoflua.lua.extension.util.LuaCachingLuaConverter;

@AutoService(LuaConverter.class)
public class EventQueueClassInstanceConverter
    extends LuaCachingLuaConverter<EventQueueClass.Instance<?>, EventQueueClassInstanceTable<?>> {
  @Inject
  private EventQueueClass luaClass;
  @Resource
  private LuaConverters converters;

  @Override
  public Instance<?> getJavaInstance(EventQueueClassInstanceTable<?> luaInstance) {
    return luaInstance.getDelegate();
  }

  @Override
  protected EventQueueClassInstanceTable<?> toLuaInstance(Instance<?> javaInstance) {
    return new EventQueueClassInstanceTable<>(javaInstance, luaClass.getTable(), converters);
  }
}
