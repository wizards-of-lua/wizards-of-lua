package net.wizardsoflua.lua.module;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.Named;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.table.PropertyTable;

public abstract class GeneratedLuaModule<D> extends PropertyTable implements Delegator<D> {
  private final D delegate;
  private final Converter converter;

  public GeneratedLuaModule(D delegate, Converter converter) {
    super(true);
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
