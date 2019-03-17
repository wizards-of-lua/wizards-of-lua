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
 * The <span class="notranslate">Time</span> module provides access to time related properties of
 * the active Spell's world.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = TimeModule.NAME, subtitle = "Accessing the Time")
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
   * The allowance is the number of lua ticks that are left before the spell or event listener is
   * broken or sent to sleep, depending on [autosleep](#autosleep).
   */
  @LuaProperty
  public long getAllowance() {
    return scheduler.getAllowance();
  }

  /**
   * The autosleep value defines whether the current spell should go to sleep automatically when its
   * allowance is exceeded.
   *
   * If this is set to false, the spell will never go to sleep automatically, but instead will be
   * broken when its allowance reaches zero. Default is true normally, but in an [event
   * interceptor](/modules/Events#intercept) 'autosleep' is always false and can't be changed.
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
    return time.getGameTime();
  }

  /**
   * The luatime is the number of lua ticks that the current spell has worked since it has been
   * casted. This includes lua ticks of event listeners.
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
   *
   * #### Example
   *
   * Printing the current date and time in ISO date-time format, such as '2011-12-03T10:15:30'.
   *
   * <code>
   * print(Time.getDate())
   * </code>
   *
   * #### Example
   *
   * Printing the current date and time with a custom format, such as '03 Oct, 2017 - 10:15:29'.
   *
   * <code>
   * print(Time.getDate("dd MMM, yyyy - HH:mm:ss"))
   * </code>
   *
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
   * Forces the current spell to sleep for the given number of game ticks.
   *
   * If the number is 0, the spell won't sleep. If the number is negative, this function will issue
   * an error. If the number is nil, the spell might go to sleep or not. This depends on the number
   * of lua ticks that are already consumed by this spell.
   *
   * The rule is as follows: the spell will be sent to sleep if the spell's allowance falls below
   * the half value of the spell's initial allowance.
   *
   * #### Example
   *
   * Sending the current spell to sleep for 100 game ticks, which are approximately 5 seconds.
   *
   * <code>
   * Time.sleep(100)
   * </code>
   *
   * Since <span class="notranslate">'sleep'</span> is a widely used function, there is a shortcut
   * for it.
   *
   * <code>
   * sleep(100)
   * </code>
   *
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
