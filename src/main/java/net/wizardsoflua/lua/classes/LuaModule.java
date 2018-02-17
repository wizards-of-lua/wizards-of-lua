package net.wizardsoflua.lua.classes;

import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.common.Named;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public abstract class LuaModule<D> extends DelegatingProxy<D> implements Named {
  public LuaModule(LuaClassLoader classLoader, D delegate) {
    super(classLoader, delegate);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  public <F extends LuaFunction & Named> void add(F function) {
    add(function.getName(), function);
  }

  public void add(String name, LuaFunction function) {
    addImmutable(name, function);
  }
}
