package net.wizardsoflua.lua.extension.util;

import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Converter;

public abstract class AbstractLuaClass<J, L extends Delegator<? extends Delegator<? extends J>>>
    extends LuaInstanceCachingLuaClass<J, L> {
  @Inject
  private Converter converter;

  /**
   * @return the value of {@link #converter}
   */
  public Converter getConverter() {
    return converter;
  }

  @Override
  public J getJavaInstance(L luaInstance) {
    Delegator<? extends J> delegate = luaInstance.getDelegate();
    J result = delegate.getDelegate();
    return result;
  }
}
