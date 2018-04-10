package net.wizardsoflua.lua.extension.spi;

import net.wizardsoflua.lua.extension.api.InitializationContext;

/**
 * @deprecated Do this using Injection
 */
@Deprecated
public interface SpellExtension {
  void initialize(InitializationContext context);
}
