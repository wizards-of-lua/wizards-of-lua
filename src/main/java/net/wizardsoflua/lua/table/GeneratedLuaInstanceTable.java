package net.wizardsoflua.lua.table;

import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.JavaInstanceWrapper;

public class GeneratedLuaInstanceTable<D extends JavaInstanceWrapper<?>>
    extends GeneratedLuaTable<D> implements JavaInstanceWrapper<Object> {
  public GeneratedLuaInstanceTable(D delegate, Table metatable, LuaConverters converters,
      boolean allowAdditionalProperties) {
    super(delegate, converters, allowAdditionalProperties);
    setMetatable(metatable);
  }

  @Override
  public Object getJavaInstance() {
    D delegate = getDelegate();
    return delegate.getJavaInstance();
  }
}
