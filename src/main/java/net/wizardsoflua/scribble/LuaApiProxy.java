package net.wizardsoflua.scribble;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.classes.common.LuaInstanceProxy;

public class LuaApiProxy<A extends LuaApiBase<D>, D> extends LuaInstanceProxy<D> {
  protected final A api;

  public LuaApiProxy(A api) {
    super(api.getLuaClass(), api.getDelegate());
    this.api = requireNonNull(api, "api == null!");
  }

  /**
   * @return the value of {@link #api}
   */
  public A getApi() {
    return api;
  }

  @Override
  public void setDelegate(D delegate) {
    super.setDelegate(delegate);
    api.setDelegate(delegate);
  }

  @Override
  public boolean isTransferable() {
    return true;
  }
}
