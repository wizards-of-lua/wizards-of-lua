package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;

public class DelegatingProxy extends DelegatingTable {

  private final Object delegate;
  private final Converters converters;

  public DelegatingProxy(Converters converters, Table metatable, Object delegate) {
    super(metatable);
    this.delegate = checkNotNull(delegate, "delegate==null!");
    this.converters = checkNotNull(converters, "converters==null!");
  }

  public Object getDelegate() {
    return delegate;
  }

  public Converters getConverters() {
    return converters;
  }

}
