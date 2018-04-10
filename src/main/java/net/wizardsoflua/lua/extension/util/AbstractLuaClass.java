package net.wizardsoflua.lua.extension.util;

import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.InitializationContext;

public abstract class AbstractLuaClass<J, L extends Delegator<? extends Delegator<? extends J>>>
    extends LuaInstanceCachingLuaClass<J, L> {
  private Converter converter;

  @Override
  public void initialize(InitializationContext context) {
    converter = context.getConverter();
  }

  /**
   * @return the value of {@link #converter}
   */
  public Converter getConverter() {
    return converter;
  }

  @Override
  protected J toJava(L luaInstance) {
    Delegator<? extends J> delegate = luaInstance.getDelegate();
    J result = delegate.getDelegate();
    return result;
  }
}
