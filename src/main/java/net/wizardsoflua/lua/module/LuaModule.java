package net.wizardsoflua.lua.module;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.extension.api.Named;

public abstract class LuaModule<D> extends DelegatingProxy<D> implements Named {
  public LuaModule(LuaClassLoader classLoader, D delegate) {
    super(classLoader, delegate);
  }

  public void installInto(Table env) {
    env.rawset(getName(), this);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  public <F extends LuaFunction & Named> void addReadOnly(F function) {
    addReadOnly(function.getName(), function);
  }

  public void addReadOnly(String name, LuaFunction function) {
    addImmutable(name, function);
  }
}
