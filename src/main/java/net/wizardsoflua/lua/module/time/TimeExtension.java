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
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Config;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.extension.api.service.LuaScheduler;
import net.wizardsoflua.lua.extension.api.service.Time;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;
import net.wizardsoflua.lua.module.types.TypesModule;

/**
 * The Time module provides access to time related properties of the active Spell's world.
 */
@GenerateLuaModuleTable
@GenerateLuaDoc(name = TimeExtension.NAME, subtitle = "Accessing the Time")
@AutoService(LuaExtension.class)
public class TimeExtension implements LuaTableExtension {
  public static final String NAME = "Time";
  @Inject
  private Config config;
  @Inject
  private Converter converter;
  @Inject
  private LuaScheduler scheduler;
  @Inject
  private Time time;

  private long sleepTrigger;

  @AfterInjection
  public void initialize() {
    sleepTrigger = config.getLuaTickLimit() / 2;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new TimeExtensionTable<>(this, converter);
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
    return scheduler.getTotalLuaTicks();
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
  @LuaFunctionDoc(args = {"ticks"}, returnType = TypesModule.NIL)
  public class SleepFunction extends AbstractFunction1 {
    public static final String NAME = "sleep";

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      Integer ticks = converter.toJavaNullable(Integer.class, arg1, 1, "ticks", NAME);
      try {
        sleep(context, ticks);
      } catch (UnresolvedControlThrowable t) {
        t.resolve(this, null);
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
