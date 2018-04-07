package net.wizardsoflua.lua.extension.api;

import javax.annotation.Nullable;

import net.wizardsoflua.lua.extension.spi.LuaModule;

public interface LuaModuleLoader {
  @Nullable
  <M extends LuaModule> M getModule(Class<M> moduleType);
}
