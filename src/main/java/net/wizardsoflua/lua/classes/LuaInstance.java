package net.wizardsoflua.lua.classes;

import net.wizardsoflua.lua.classes.common.ModifiableDelegator;

public class LuaInstance<J> extends ModifiableDelegator<J> implements JavaInstanceWrapper<J> {
  public LuaInstance(J javaInstance) {
    super(javaInstance);
  }

  @Override
  public J getJavaInstance() {
    return getDelegate();
  }
}
