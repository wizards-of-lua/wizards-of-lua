package net.wizardsoflua.lua.module.time;

import javax.annotation.Nullable;

import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.module.LuaModuleApi;
import net.wizardsoflua.lua.module.types.Types;

/**
 * The Time module provides access to time related properties of the active Spell's world.
 */
@GenerateLuaModule(name = "Time")
@GenerateLuaDoc(subtitle = "Accessing the Time")
public class TimeApi extends LuaModuleApi<Time> {
  public TimeApi(LuaClassLoader classLoader, Time delegate) {
    super(classLoader, delegate);
  }

  /**
   * The allowance is the number of lua ticks that are left before the active spell must sleep for
   * at least one game tick.
   */
  @LuaProperty
  public long getAllowance() {
    return delegate.getAllowance();
  }

  /**
   * The autosleep value defines whether the current spell should go to sleep automatically when its
   * allowance is exceeded. If this is set to false, the spell will never go to sleep automatically,
   * but instead will be broken when its allowance falls below zero. Default is true.
   */
  @LuaProperty
  public boolean isAutosleep() {
    return delegate.isAutosleep();
  }

  @LuaProperty
  public void setAutosleep(boolean autosleep) {
    delegate.setAutosleep(autosleep);
  }

  /**
   * The gametime is the number of game ticks that have passed since the world has been created.
   */
  @LuaProperty
  public long getGametime() {
    return delegate.getGameTotalTime();
  }

  /**
   * The luatime is the number of lua ticks that the current spell has worked since it has been
   * casted.
   */
  @LuaProperty
  public long getLuatime() {
    return delegate.getLuaTicks();
  }

  /**
   * The realtime is the number of milliseconds that have passed since January 1st, 1970.
   */
  @LuaProperty
  public long getRealtime() {
    return delegate.getRealtime();
  }

  /**
   * Returns a string with the current real date and time. If you want you can change the format by
   * providing an optional format string.
   */
  @LuaFunction
  public String getDate(@Nullable String pattern) {
    return delegate.getDate(pattern);
  }

  /**
   * Forces the current spell to sleep for the given amount of game ticks.
   */
  @LuaFunction(name = SleepFunction.NAME)
  @LuaFunctionDoc(args = {"ticks"}, returnType = Types.NIL_META)
  public class SleepFunction extends AbstractFunction1 {
    public static final String NAME = "sleep";

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      Integer ticks = getConverters().toJavaNullable(Integer.class, arg1, 1, "ticks", NAME);
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
        getClassLoader().getCurrentSchedulingContext().pauseIfRequested(context);
      } catch (UnresolvedControlThrowable ex) {
        throw ex.resolve(this, null);
      }
      context.getReturnBuffer().setTo();
    }
  }
}
