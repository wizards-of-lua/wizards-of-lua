package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;

import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class UnsupportedFunction extends AbstractFunctionAnyArg {
  private final String name;
  private final String metatableName;

  public UnsupportedFunction(String name, String metatableName) {
    this.name = name;
    this.metatableName = metatableName;
  }

  @Override
  public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
    BasicLib.error().invoke(context, format("%s not supported for class %s", name, metatableName));
  }

  @Override
  public void resume(ExecutionContext context, Object suspendedState)
      throws ResolvedControlThrowable {
    throw new NonsuspendableFunctionException();
  }

}
