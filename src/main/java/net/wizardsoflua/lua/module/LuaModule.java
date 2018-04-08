package net.wizardsoflua.lua.module;

import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.extension.api.Named;

public abstract class LuaModule<D> extends DelegatingProxy<D> {
  public LuaModule(LuaClassLoader classLoader, D delegate) {
    super(classLoader, delegate);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  public <F extends LuaFunction & Named> void addReadOnly(F function) {
    addReadOnly(function.getName(), function);
  }

  public void addReadOnly(String name, LuaFunction function) {
    addReadOnly(name, () -> function);
  }
}
