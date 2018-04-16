package net.wizardsoflua.lua.extension.api.service;

import javax.annotation.Nullable;

import net.wizardsoflua.lua.extension.spi.SpellExtension;

public interface SpellExtensions {
  @Nullable
  <E extends SpellExtension> E getSpellExtension(Class<E> extensionClass);
}
