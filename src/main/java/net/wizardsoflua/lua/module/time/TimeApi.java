package net.wizardsoflua.lua.module.time;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction1;

@GenerateLuaModule(name = "Time2")
public class TimeApi {
  private Time delegate;
  private Converters converters;

  public Converters getConverters() {
    return converters;
  }

  @LuaProperty
  public boolean isAutoSleep() {
    return delegate.isAutoSleep();
  }

  @LuaProperty
  public void setAutoSleep(boolean isAutoSleep) {
    delegate.setAutoSleep(isAutoSleep);
  }

  @LuaProperty
  public long getGameTotalTime() {
    return delegate.getGameTotalTime();
  }

  @LuaProperty
  public long getLuaTicks() {
    return delegate.getLuaTicks();
  }

  @LuaProperty
  public long getAllowance() {
    return delegate.getAllowance();
  }

  @LuaProperty
  public long getRealtime() {
    return delegate.getRealtime();
  }

  @LuaFunction
  public String getDate(String pattern) {
    return delegate.getDate(pattern);
  }

  public void onCreateModule(TimeModule2 module) {
    module.addReadOnly(new SleepFunction());
  }

  @LuaFunction
  public class SleepFunction extends NamedFunction1 {
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
