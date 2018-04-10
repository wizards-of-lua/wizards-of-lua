package net.wizardsoflua.lua.classes.eventqueue.scribble;

import javax.annotation.Generated;

import com.google.common.collect.ImmutableSet;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.function.NamedFunction0;
import net.wizardsoflua.lua.table.GeneratedLuaTable;

@Generated("GenerateLuaModule")
public class EventQueueClassInstanceTable extends GeneratedLuaTable<EventQueueClass.Instance<?>> {
  public EventQueueClassInstanceTable(EventQueueClass.Instance<?> delegate, Converter converter) {
    super(delegate, converter, false);
    addReadOnly("names", this::getNames);
    addFunction(new DisconnectFunction());
    addFunction(new IsEmptyFunction());
    addFunction(new LatestFunction());
    addFunction("next", delegate.new NextFunction());
  }

  private Object getNames() {
    ImmutableSet<String> result = getDelegate().getNames();
    return getConverter().toLuaNullable(result);
  }

  private class DisconnectFunction extends NamedFunction0 {
    @Override
    public String getName() {
      return "disconnect";
    }

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      getDelegate().disconnect();
      context.getReturnBuffer().setTo();
    }
  }

  private class IsEmptyFunction extends NamedFunction0 {
    @Override
    public String getName() {
      return "isEmpty";
    }

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      boolean result = getDelegate().isEmpty();
      context.getReturnBuffer().setTo(result);
    }
  }

  private class LatestFunction extends NamedFunction0 {
    @Override
    public String getName() {
      return "latest";
    }

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Event result = getDelegate().latest();
      Object luaResult = getConverter().toLuaNullable(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }
}
