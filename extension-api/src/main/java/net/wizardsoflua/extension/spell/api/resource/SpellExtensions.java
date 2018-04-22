package net.wizardsoflua.extension.spell.api.resource;

import javax.annotation.Nullable;

import net.wizardsoflua.extension.spell.spi.SpellExtension;

@Deprecated
public interface SpellExtensions {
  @Nullable
  <E extends SpellExtension> E getSpellExtension(Class<E> extensionClass);
}
