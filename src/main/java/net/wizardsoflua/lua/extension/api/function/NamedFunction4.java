package net.wizardsoflua.lua.extension.api.function;

import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction4;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.Named;

public abstract class NamedFunction4 extends AbstractFunction4 implements Named {
  @Override
  public void resume(ExecutionContext context, Object suspendedState)
      throws ResolvedControlThrowable {
    throw new NonsuspendableFunctionException(getClass());
  }
}
