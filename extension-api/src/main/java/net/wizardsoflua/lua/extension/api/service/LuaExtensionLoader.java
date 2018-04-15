package net.wizardsoflua.lua.extension.api.service;

import javax.annotation.Nullable;

import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.spi.SpellExtension;

public interface LuaExtensionLoader {
  @Nullable
  <M extends LuaExtension> M getLuaExtension(Class<M> moduleType);

  @Nullable
  <E extends SpellExtension> E getSpellExtension(Class<E> extensionClass);
}
