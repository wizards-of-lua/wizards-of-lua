package net.wizardsoflua.lua.classes.eventqueue.scribble;

import javax.annotation.Generated;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.function.NamedFunction1;
import net.wizardsoflua.lua.table.GeneratedLuaTable;

@Generated("GenerateLuaTable")
public class EventQueueClassTable extends GeneratedLuaTable<EventQueueClass> {
  public EventQueueClassTable(EventQueueClass delegate, Converter converter) {
    super(delegate, converter, true);
    addFunction(new DisconnectFunction());
    addFunction(new IsEmptyFunction());
    addFunction(new LatestFunction());
    addFunction("next", new EventQueueClass.Instance.NextFunction(delegate));
  }

  private class DisconnectFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "disconnect";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      EventQueueClass.Instance<?> self =
          getConverter().toJava(EventQueueClass.Instance.class, arg1, 1, "self", getName());
      self.disconnect();
      context.getReturnBuffer().setTo();
    }
  }

  private class IsEmptyFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "isEmpty";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      EventQueueClass.Instance<?> self =
          getConverter().toJava(EventQueueClass.Instance.class, arg1, 1, "self", getName());
      boolean result = self.isEmpty();
      context.getReturnBuffer().setTo(result);
    }
  }

  private class LatestFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "latest";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      EventQueueClass.Instance<?> self =
          getConverter().toJava(EventQueueClass.Instance.class, arg1, 1, "self", getName());
      Event result = self.latest();
      Object luaResult = getConverter().toLuaNullable(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }
}
