package net.wizardsoflua.lua.classes;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.classes.common.LuaInstance;

public class GeneratedLuaInstance<A extends LuaClassApi<D>, D> extends LuaInstance<D> {
  protected final A api;

  public GeneratedLuaInstance(A api) {
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
    return api.isTransferable();
  }
}
