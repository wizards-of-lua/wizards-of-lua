package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.ExceptionHandler;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.api.resource.Time;

public class SpellThread {
  @Resource
  private Time time;
  @Resource
  private LuaScheduler scheduler;
  @Resource
  private ExceptionHandler exceptionHandler;

  private String name;
  private long luaTickLimit;
  private long wakeUpTime;
  private CheckedRunnable nextStep;

  public SpellThread(Injector injector, String name, long luaTickLimit, LuaFunction function,
      Object... args) {
    this(injector, name, luaTickLimit, createRunnable(injector, luaTickLimit, function, args));
  }

  private static CheckedRunnable createRunnable(Injector injector, long luaTickLimit,
      LuaFunction function, Object... args) {
    LuaScheduler scheduler = injector.getResource(LuaScheduler.class);
    return () -> scheduler.call(luaTickLimit, function, args);
  }

  public SpellThread(Injector injector, String name, long luaTickLimit, CheckedRunnable runnable) {
    injector.injectMembers(this);
    this.name = name;
    this.luaTickLimit = luaTickLimit;
    nextStep = runnable;
  }

  public String getName() {
    return name;
  }

  public boolean tick() throws Exception {
    try {
      if (wakeUpTime > time.getTotalWorldTime()) {
        return true;
      }
      nextStep.run();
      nextStep = CheckedRunnable.DO_NOTHING;
      return false;
    } catch (CallFellAsleepException ex) {
      nextStep = sleep(ex.getSleepDuration(), ex.getContinuation());
    } catch (CallPausedException ex) {
      nextStep = sleep(1, ex.getContinuation());
    }
    return true;
  }

  private CheckedRunnable sleep(int sleepDuration, Continuation continuation) {
    wakeUpTime = time.getTotalWorldTime() + sleepDuration;
    return () -> {
      scheduler.resume(luaTickLimit, continuation);
    };
  }


}
