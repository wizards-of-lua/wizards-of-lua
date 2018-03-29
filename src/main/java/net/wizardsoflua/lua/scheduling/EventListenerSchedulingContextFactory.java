package net.wizardsoflua.lua.scheduling;

import static net.wizardsoflua.lua.scheduling.LuaExecutor.Type.EVENT_LISTENER;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.lua.scheduling.LuaExecutor.Type;

public class EventListenerSchedulingContextFactory implements LuaSchedulingContextFactory {
  private int luaTickLimit;

  public EventListenerSchedulingContextFactory(int luaTickLimit) {
    this.luaTickLimit = luaTickLimit;
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit) {
      @Override
      public boolean isAutosleep() {
        return false;
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        throw new IllegalOperationAttemptException("attempt to set autosleep");
      }

      @Override
      public Type getLuaExecutorType() {
        return EVENT_LISTENER;
      }

      @Override
      public void pauseIfRequested(ExecutionContext context) {
        throw new IllegalOperationAttemptException("attempt to sleep");
      }
    };
  }
}
