package net.wizardsoflua.lua.scheduling;

import static net.sandius.rembulan.exec.DirectCallExecutor.newExecutor;

import javax.annotation.Nullable;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.runtime.SchedulingContext;

public class LuaExecutor {
  public enum Type {
    MAIN {
      @Override
      protected DirectCallExecutor getExecutor(LuaExecutor luaExecutor) {
        return luaExecutor.mainExecutor;
      }
    },
    EVENT_LISTENER {
      @Override
      protected DirectCallExecutor getExecutor(LuaExecutor luaExecutor) {
        return luaExecutor.eventListenerExecutor;
      }
    };

    protected abstract DirectCallExecutor getExecutor(LuaExecutor luaExecutor);
  }

  private final AggregatingSchedulingContext context;
  private final DirectCallExecutor mainExecutor;
  private final DirectCallExecutor eventListenerExecutor;
  private @Nullable LuaSchedulingContext currentSchedulingContext;

  public LuaExecutor(int mainLuaTickLimit, int eventListenerLuaTickLimit) {
    context = new AggregatingSchedulingContext();
    mainExecutor = newExecutor(wrap(new MainSchedulingContextFactory(mainLuaTickLimit, context)));
    eventListenerExecutor =
        newExecutor(wrap(new EventListenerSchedulingContextFactory(eventListenerLuaTickLimit)));
  }

  private LuaSchedulingContextFactory wrap(LuaSchedulingContextFactory delegate) {
    return new LuaSchedulingContextFactory() {
      @Override
      public LuaSchedulingContext newInstance() {
        currentSchedulingContext = delegate.newInstance();
        return currentSchedulingContext;
      }
    };
  }

  public Object[] call(Type type, StateContext stateContext, Object fn, Object... args)
      throws CallException, CallPausedException, InterruptedException {
    DirectCallExecutor executor = type.getExecutor(this);
    LuaSchedulingContext previousSchedulingContext = currentSchedulingContext;
    try {
      return executor.call(stateContext, fn, args);
    } finally {
      currentSchedulingContext = previousSchedulingContext;
    }
  }

  public Object[] resume(Type type, Continuation continuation)
      throws CallException, CallPausedException, InterruptedException {
    DirectCallExecutor executor = type.getExecutor(this);
    LuaSchedulingContext previousSchedulingContext = currentSchedulingContext;
    try {
      return executor.resume(continuation);
    } finally {
      currentSchedulingContext = previousSchedulingContext;
    }
  }

  public @Nullable LuaSchedulingContext getCurrentSchedulingContext() {
    return currentSchedulingContext;
  }

  public void addSchedulingContext(SchedulingContext context) {
    this.context.addSchedulingContext(context);
  }
}
