package net.wizardsoflua.lua.table;

import static com.google.common.base.Preconditions.checkNotNull;

import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.common.Delegator;

public class GeneratedLuaTable<D> extends PropertyTable implements Delegator<D> {
  private final D delegate;
  private final LuaConverters converters;

  public GeneratedLuaTable(D delegate, LuaConverters converters,
      boolean allowAdditionalProperties) {
    super(allowAdditionalProperties);
    this.delegate = checkNotNull(delegate, "delegate == null!");
    this.converters = checkNotNull(converters, "converters == null!");
  }

  @Override
  public D getDelegate() {
    return delegate;
  }

  public LuaConverters getConverters() {
    return converters;
  }
}
