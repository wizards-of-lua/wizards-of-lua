package net.wizardsoflua.lua.module.time;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.LuaModule;
import net.wizardsoflua.lua.function.NamedFunction1;

public class TimeModule extends LuaModule<Time> {
  public static TimeModule installInto(Table env, LuaClassLoader classLoader, Time time) {
    TimeModule result = new TimeModule(classLoader, time);
    env.rawset(result.getName(), result);
    return result;
  }

  public TimeModule(LuaClassLoader classLoader, Time delegate) {
    super(classLoader, delegate);
    addReadOnly("allowance", () -> delegate.getAllowance());
    add("autosleep", () -> delegate.isAutosleep(), this::setAutosleep);
    addReadOnly("luatime", () -> delegate.getLuaTicks());
    addReadOnly("gametime", () -> delegate.getGameTotalTime());
    addReadOnly("realtime", () -> delegate.getRealtime());

    addReadOnly(new SleepFunction());
    addReadOnly(new GetDateFunction());
  }

  @Override
  public String getName() {
    return "Time";
  }

  public void setAutosleep(Object luaObj) {
    boolean value = getConverters().toJava(boolean.class, luaObj, "autosleep");
    delegate.setAutosleep(value);
  }

  private class GetDateFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "getDate";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String format = getConverters().toJavaNullable(String.class, arg1, 1, "format", getName());
      String result = delegate.getDate(format);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class SleepFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "sleep";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        return; // ignore call
      }
      int ticks = getConverters().toJava(int.class, arg1, 1, "ticks", getName());
      delegate.startSleep(ticks);
      execute(context);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      execute(context);
    }

    private void execute(ExecutionContext context) throws ResolvedControlThrowable {
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable ex) {
        throw ex.resolve(this, null);
      }
      context.getReturnBuffer().setTo();
    }
  }
}
