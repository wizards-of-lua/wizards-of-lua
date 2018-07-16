package net.wizardsoflua.lua.module.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.api.resource.Time;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;

/**
 * The Time module provides access to time related properties of the active Spell's world.
 */
@GenerateLuaModuleTable
@GenerateLuaDoc(name = TimeModule.NAME, subtitle = "Accessing the Time")
@AutoService(SpellExtension.class)
public class TimeModule extends LuaTableExtension {
  public static final String NAME = "Time";
  @Resource
  private LuaConverters converters;
  @Resource
  private LuaScheduler scheduler;
  @Resource
  private Time time;

  private long sleepTrigger;
  private long luatime;

  public void init(@Resource Config config) {
    sleepTrigger = config.getLuaTickLimit() / 2;
    scheduler.addTickListener(ticks -> luatime += ticks);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new TimeModuleTable<>(this, converters);
  }

  /**
   * The allowance is the number of lua ticks that are left before the active spell must sleep for
   * at least one game tick.
   */
  @LuaProperty
  public long getAllowance() {
    return scheduler.getAllowance();
  }

  /**
   * The autosleep value defines whether the current spell should go to sleep automatically when its
   * allowance is exceeded. If this is set to false, the spell will never go to sleep automatically,
   * but instead will be broken when its allowance falls below zero. Default is true.
   */
  @LuaProperty
  public boolean isAutosleep() {
    return scheduler.isAutosleep();
  }

  @LuaProperty
  public void setAutosleep(boolean autosleep) {
    scheduler.setAutosleep(autosleep);
  }

  /**
   * The gametime is the number of game ticks that have passed since the world has been created.
   */
  @LuaProperty
  public long getGametime() {
    return time.getTotalWorldTime();
  }

  /**
   * The luatime is the number of lua ticks that the current spell has worked since it has been
   * casted.
   */
  @LuaProperty
  public long getLuatime() {
    return luatime;
  }

  /**
   * The realtime is the number of milliseconds that have passed since January 1st, 1970.
   */
  @LuaProperty
  public long getRealtime() {
    return time.getClock().millis();
  }

  /**
   * Returns a string with the current real date and time. If you want you can change the format by
   * providing an optional format string.
   */
  @LuaFunction
  public String getDate(@Nullable String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    if (pattern != null) {
      formatter = DateTimeFormatter.ofPattern(pattern);
    }
    String result = LocalDateTime.now(time.getClock()).format(formatter);
    return result;
  }

  public void sleep(ExecutionContext context, @Nullable Integer ticks)
      throws UnresolvedControlThrowable {
    if (ticks == null) {
      if (getAllowance() < sleepTrigger) {
        ticks = 1;
      } else {
        return;
      }
    }
    if (ticks < 0) {
      throw new IllegalOperationAttemptException("attempt to sleep a negative amount of ticks");
    }
    scheduler.sleep(context, ticks);
  }

  /**
   * Forces the current spell to sleep for the given amount of game ticks.
   */
  @LuaFunction(name = SleepFunction.NAME)
  @LuaFunctionDoc(args = {"ticks"}, returnType = LuaTypes.NIL)
  public class SleepFunction extends AbstractFunction1 {
    public static final String NAME = "sleep";

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      Integer ticks = converters.toJavaNullable(Integer.class, arg1, 1, "ticks", NAME);
      try {
        sleep(context, ticks);
      } catch (UnresolvedControlThrowable t) {
        throw t.resolve(this, null);
      }
      execute(context);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      execute(context);
    }

    private void execute(ExecutionContext context) {
      context.getReturnBuffer().setTo();
    }
  }
}
