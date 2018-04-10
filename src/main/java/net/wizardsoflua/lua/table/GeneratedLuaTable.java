package net.wizardsoflua.lua.table;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.Named;

public class GeneratedLuaTable<D> extends PropertyTable implements Delegator<D> {
  private final D delegate;
  private final Converter converter;

  public GeneratedLuaTable(D delegate, Converter converter, boolean allowAdditionalProperties) {
    super(allowAdditionalProperties);
    this.delegate = checkNotNull(delegate, "delegate == null!");
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public D getDelegate() {
    return delegate;
  }

  public Converter getConverter() {
    return converter;
  }

  public <F extends LuaFunction & Named> void addReadOnly(F function) {
    addReadOnly(function.getName(), function);
  }

  public void addReadOnly(String name, LuaFunction function) {
    addReadOnly(name, () -> function);
  }
}
