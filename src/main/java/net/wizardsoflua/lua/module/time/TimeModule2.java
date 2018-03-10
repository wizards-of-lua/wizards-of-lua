package net.wizardsoflua.lua.module.time;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.LuaModule;
import net.wizardsoflua.lua.function.NamedFunction1;

public class TimeModule2 extends LuaModule<TimeApi> {
  public TimeModule2(LuaClassLoader classLoader, TimeApi delegate) {
    super(classLoader, delegate);
    addReadOnly("allowance", () -> delegate.getAllowance());
    add("autosleep", () -> delegate.isAutosleep(), this::setAutoSleep);
    addReadOnly("gametime", () -> delegate.getGametime());
    addReadOnly("luatime", () -> delegate.getLuatime());
    addReadOnly("realtime", () -> delegate.getRealtime());
    addReadOnly(new GetDateFunction());
    addReadOnly("sleep", delegate.new SleepFunction());
  }

  @Override
  public String getName() {
    return "Time";
  }

  public void setAutoSleep(Object luaObj) {
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
}
