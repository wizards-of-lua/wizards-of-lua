package net.wizardsoflua.lua.extension.util;

import net.wizardsoflua.lua.classes.common.Delegator;

public abstract class DelegatorCachingLuaClass<J>
    extends LuaInstanceCachingLuaClass<J, Delegator<? extends Delegator<? extends J>>> {
  @Override
  public J getJavaInstance(Delegator<? extends Delegator<? extends J>> luaInstance) {
    Delegator<? extends J> delegate = luaInstance.getDelegate();
    J result = delegate.getDelegate();
    return result;
  }
}
