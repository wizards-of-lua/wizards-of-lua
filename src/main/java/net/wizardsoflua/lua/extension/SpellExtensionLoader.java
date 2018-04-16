package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.util.ServiceConfigurationError;

import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.api.service.SpellExtensions;
import net.wizardsoflua.lua.extension.spi.LuaConverter;
import net.wizardsoflua.lua.extension.spi.SpellExtension;

public class SpellExtensionLoader implements SpellExtensions {
  private final ClassIndex serviceProvider = new ClassIndex();
  private final Injector injector;
  private final LuaConverters converters;

  public SpellExtensionLoader(Injector injector, LuaConverters converters) {
    this.injector = checkNotNull(injector, "injector == null!");
    this.converters = requireNonNull(converters, "converters == null!");
  }

  public Injector getInjector() {
    return injector;
  }

  public void installExtensions() {
    ServiceLoader.load(LuaConverter.class).forEach(this::registerLuaConverter);
    ServiceLoader.load(SpellExtension.class).forEach(this::getSpellExtension);
  }

  private <C extends LuaConverter<?, ?>> void registerLuaConverter(Class<C> converterClass) {
    C converter = getSpellExtension(converterClass);
    converters.registerLuaConverter(converter);
  }

  @Override
  public <E extends SpellExtension> E getSpellExtension(Class<E> extensionClass) {
    E extension = serviceProvider.get(extensionClass);
    if (extension == null) {
      extension = newInstance(extensionClass);
      serviceProvider.add(extension);
      injector.inject(extension);
    }
    return extension;
  }

  private static <P> P newInstance(Class<P> cls) throws ServiceConfigurationError {
    try {
      return cls.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      String message = "Provider " + cls + " could not be instantiated";
      throw new ServiceConfigurationError(SpellExtension.class.getName() + ": " + message, ex);
    }
  }
}
