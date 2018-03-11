package net.wizardsoflua.lua.module;

public abstract class GeneratedLuaModule<D extends LuaModuleApi<?>> extends LuaModule<D> {
  public GeneratedLuaModule(D delegate) {
    super(delegate.getClassLoader(), delegate);
  }

  @Override
  public boolean isTransferable() {
    return delegate.isTransferable();
  }
}
