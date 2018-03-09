package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.SchedulingContext;

public class EventListenerSchedulingContextFactory implements LuaSchedulingContextFactory {
  private int luaTickLimit;
  private final SchedulingContext context;

  public EventListenerSchedulingContextFactory(int luaTickLimit, SchedulingContext context) {
    this.luaTickLimit = luaTickLimit;
    this.context = new SchedulingContext() {
      @Override
      public boolean shouldPause() {
        if (context.shouldPause()) {
          throw new IllegalOperationAttemptException("attempt to sleep");
        }
        return false;
      }

      @Override
      public void registerTicks(int ticks) {
        context.registerTicks(ticks);
      }
    };
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit, context) {
      @Override
      public boolean isAutosleep() {
        return false;
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        throw new IllegalOperationAttemptException("attempt to set autosleep");
      }
    };
  }
}
