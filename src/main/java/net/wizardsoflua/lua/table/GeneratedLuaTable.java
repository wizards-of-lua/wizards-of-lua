package net.wizardsoflua.lua.table;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;

public class GeneratedLuaTable<D> extends PropertyTable implements Delegator<D> {
  private final D delegate;
  private final LuaConverters converters;

  public GeneratedLuaTable(D delegate, LuaConverters converters,
      boolean allowAdditionalProperties) {
    super(allowAdditionalProperties);
    this.delegate = checkNotNull(delegate, "delegate == null!");
    this.converters = checkNotNull(converters, "converters == null!");
  }

  public GeneratedLuaTable(D delegate, Table metatable, LuaConverters converters,
      boolean allowAdditionalProperties) {
    this(delegate, converters, allowAdditionalProperties);
    setMetatable(metatable);
  }

  @Override
  public D getDelegate() {
    return delegate;
  }

  public LuaConverters getConverters() {
    return converters;
  }
}
