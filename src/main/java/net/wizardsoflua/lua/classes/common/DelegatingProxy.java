package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;

public abstract class DelegatingProxy<D> extends DelegatingTable {
  protected final D delegate;
  protected final Converters converters;

  public DelegatingProxy(Converters converters, Table metatable, D delegate) {
    super(metatable);
    this.delegate = checkNotNull(delegate, "delegate==null!");
    this.converters = checkNotNull(converters, "converters==null!");
  }

  public D getDelegate() {
    return delegate;
  }

  public Converters getConverters() {
    return converters;
  }

  public abstract boolean isTransferable();

}
