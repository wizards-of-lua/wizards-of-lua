package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.lua.scheduling.LuaExecutor.Type.MAIN;

import net.sandius.rembulan.runtime.SchedulingContext;
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
    return new LuaSchedulingContext(luaTickLimit, context) {
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
    };
  }
}
