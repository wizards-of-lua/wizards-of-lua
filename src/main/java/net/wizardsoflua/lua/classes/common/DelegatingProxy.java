package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public abstract class DelegatingProxy<D> extends DelegatingTable {
  private final LuaClassLoader classLoader;
  protected D delegate;

  public DelegatingProxy(LuaClassLoader classLoader, @Nullable Table metaTable, D delegate) {
    super(metaTable);
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public Converters getConverters() {
    return classLoader.getConverters();
  }

  public void setDelegate(D delegate) {
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public D getDelegate() {
    return delegate;
  }

  public abstract boolean isTransferable();
}
