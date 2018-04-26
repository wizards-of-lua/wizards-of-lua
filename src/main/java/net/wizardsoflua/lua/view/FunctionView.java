package net.wizardsoflua.lua.view;

import static java.util.Objects.requireNonNull;

import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.module.time.TimeModule;

public class FunctionView extends AbstractFunction0 implements View {
  private final LuaFunction remoteFunction;
  private final ViewFactory remoteViewFactory;

  public FunctionView(LuaFunction remoteFunction, ViewFactory remoteViewFactory) {
    this.remoteFunction = requireNonNull(remoteFunction, "remoteFunction == null!");
    this.remoteViewFactory = requireNonNull(remoteViewFactory, "remoteViewFactory == null!");
  }

  @Override
  public Object getRemoteObject() {
    return remoteFunction;
  }

  @Override
  public ViewFactory getRemoteViewFactory() {
    return remoteViewFactory;
  }

  /**
   * We don't want wizards to call functions that were defined in different spells, because these
   * functions would be executed using the env of the declaring spell. This could cause unexpected
   * behaviour, especially when interacting with the {@link TimeModule}.
   */
  @Override
  public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
    throw new IllegalOperationAttemptException("attempt to call a function from a different spell");
  }

  @Override
  public void resume(ExecutionContext context, Object suspendedState)
      throws ResolvedControlThrowable {
    throw new NonsuspendableFunctionException();
  }
}
