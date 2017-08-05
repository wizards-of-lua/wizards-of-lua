package net.wizardsoflua.lua.dependency;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.runtime.LuaFunction;

public class ModuleDependencies {
  private final List<ModuleDependency> elements = new ArrayList<>();

  public ModuleDependencies() {}

  public void add(ModuleDependency dependency) {
    elements.add(dependency);
  }

  public void installModules(Table env, DirectCallExecutor executor, StateContext stateContext)
      throws CallException, CallPausedException, InterruptedException {
    LuaFunction requireFunction =
        checkNotNull((LuaFunction) env.rawget("require"), "Missing require function!");
    for (ModuleDependency element : elements) {
      executor.call(stateContext, requireFunction, element.getName());
    }
  }

}
