package net.wizardsoflua.lua.module;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.Transferable;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public class LuaModuleApi<D> implements Transferable {
  protected final LuaClassLoader classLoader;
  protected D delegate;

  public LuaModuleApi(LuaClassLoader classLoader, D delegate) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    this.delegate = requireNonNull(delegate, "delegate == null!");
  }

  public LuaClassLoader getClassLoader() {
    return classLoader;
  }

  public D getDelegate() {
    return delegate;
  }

  /**
   * @param delegate the new value for {@link #delegate}
   */
  public void setDelegate(D delegate) {
    this.delegate = requireNonNull(delegate, "delegate == null!");
  }

  public Converters getConverters() {
    return classLoader.getConverters();
  }

  @Override
  public boolean isTransferable() {
    return false;
  }
}
