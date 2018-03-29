package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.lua.scheduling.LuaExecutor.Type.MAIN;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.scheduling.LuaExecutor.Type;

public class MainSchedulingContextFactory implements LuaSchedulingContextFactory {
  private int luaTickLimit;
  private final SchedulingContext context;
  private boolean autosleep = true;

  public MainSchedulingContextFactory(int luaTickLimit, SchedulingContext context) {
    this.luaTickLimit = luaTickLimit;
    this.context = checkNotNull(context, "context == null!");
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit) {
      @Override
      public void registerTicks(int ticks) {
        super.registerTicks(ticks);
        context.registerTicks(ticks);
      }

      @Override
      public boolean shouldPause() {
        return super.shouldPause() || context.shouldPause();
      }

      @Override
      public boolean isAutosleep() {
        return autosleep;
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        MainSchedulingContextFactory.this.autosleep = autosleep;
      }

      @Override
      public Type getLuaExecutorType() {
        return MAIN;
      }

      @Override
      public void pauseIfRequested(ExecutionContext context) throws UnresolvedControlThrowable {
        context.pauseIfRequested();
      }
    };
  }
}
