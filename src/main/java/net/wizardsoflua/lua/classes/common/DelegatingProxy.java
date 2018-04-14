package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.wizardsoflua.lua.Transferable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.table.PropertyTable;

public abstract class DelegatingProxy<D> extends PropertyTable
    implements Transferable, Delegator<D> {
  protected final LuaClassLoader classLoader;
  protected D delegate;

  public DelegatingProxy(LuaClassLoader classLoader, D delegate) {
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public LuaClassLoader getClassLoader() {
    return classLoader;
  }

  public Converter getConverter() {
    return classLoader.getConverters();
  }

  public void setDelegate(D delegate) {
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  @Override
  public D getDelegate() {
    return delegate;
  }
}
