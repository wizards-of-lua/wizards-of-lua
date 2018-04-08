package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class PausableSchedulingContextFactory implements LuaSchedulingContextFactory {
  private long luaTickLimit;
  private final Context context;

  public interface Context extends LuaSchedulingContext.Context {
    boolean shouldPause();

    boolean isAutosleep();

    void setAutosleep(boolean autosleep);
  }

  public PausableSchedulingContextFactory(long luaTickLimit, Context context) {
    this.luaTickLimit = luaTickLimit;
    this.context = checkNotNull(context, "context == null!");
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit, context) {
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
        return context.isAutosleep();
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        context.setAutosleep(autosleep);
      }

      @Override
      public void pause(ExecutionContext context) throws UnresolvedControlThrowable {
        context.pause();
      }

      @Override
      public void pauseIfRequested(ExecutionContext context) throws UnresolvedControlThrowable {
        context.pauseIfRequested();
      }
    };
  }
}
