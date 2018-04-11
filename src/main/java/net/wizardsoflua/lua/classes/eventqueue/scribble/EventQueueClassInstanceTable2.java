package net.wizardsoflua.lua.classes.eventqueue.scribble;

import javax.annotation.Generated;

import com.google.common.collect.ImmutableSet;

import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.table.GeneratedLuaTable;

@Generated("GenerateLuaTable")
public class EventQueueClassInstanceTable2 extends GeneratedLuaTable<EventQueueClass.Instance<?>> {
  public EventQueueClassInstanceTable2(EventQueueClass.Instance<?> delegate, Converter converter) {
    super(delegate, converter, false);
    addReadOnly("names", this::getNames);
  }

  private Object getNames() {
    ImmutableSet<String> result = getDelegate().getNames();
    return getConverter().toLuaNullable(result);
  }
}
