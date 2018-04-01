package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

public class UnsupportedFunction extends NamedFunctionAnyArg {
  private final String name;
  private final String metatableName;

  public UnsupportedFunction(String name, String metatableName) {
    this.name = requireNonNull(name, "name == null!");
    this.metatableName = requireNonNull(metatableName, "metatableName == null!");
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
    BasicLib.error().invoke(context, format("%s not supported for class %s", name, metatableName));
  }
}
