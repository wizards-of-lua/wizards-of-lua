package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.SpellExtensions;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.extension.spell.spi.SpellExtension;

public class SpellExtensionLoader implements SpellExtensions {
  private final InjectionScope injectionScope;
  private final LuaConverters converters;

  public SpellExtensionLoader(InjectionScope injectionScope, LuaConverters converters) {
    this.injectionScope = checkNotNull(injectionScope, "injectionScope == null!");
    this.converters = requireNonNull(converters, "converters == null!");
  }

  public Injector getInjector() {
    return injectionScope.getResource(Injector.class);
  }

  public void loadExtensions() {
    ServiceLoader.load(LuaConverter.class).forEach(this::registerLuaConverter);
    ServiceLoader.load(SpellExtension.class).forEach(this::getSpellExtension);
  }

  private <C extends LuaConverter<?, ?>> void registerLuaConverter(Class<C> converterClass) {
    C converter = getSpellExtension(converterClass);
    converters.registerLuaConverter(converter);
  }

  @Override
  public <E extends SpellExtension> E getSpellExtension(Class<E> extensionClass) {
    return injectionScope.getInstance(extensionClass);
  }
}
