package net.wizardsoflua.scribble;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.classes.common.LuaInstanceProxy;

public class LuaApiProxy<A extends LuaApi<D>, D> extends LuaInstanceProxy<D> {
  protected final A api;

  public LuaApiProxy(A api) {
    super(api.getLuaClass(), api.getDelegate());
    this.api = requireNonNull(api, "api == null!");
  }

  @Override
  public boolean isTransferable() {
    return true;
  }
}
