package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.util.ServiceConfigurationError;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.extension.spi.LuaConverter;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public class SpellExtensionLoader
    implements net.wizardsoflua.lua.extension.api.service.LuaExtensionLoader {
  private final ClassIndex extensions = new ClassIndex();
  private final ClassIndex converterExtensions = new ClassIndex();
  private final ClassIndex luaExtensions = new ClassIndex();
  private final Table env;
  private final ServiceInjector injector;
  private final Converters converters;

  public SpellExtensionLoader(Table env, ServiceInjector injector, Converters converters) {
    this.env = checkNotNull(env, "env == null!");
    this.injector = checkNotNull(injector, "injector == null!");
    this.converters = requireNonNull(converters, "converters == null!");
  }

  public ServiceInjector getInjector() {
    return injector;
  }

  public void installExtensions() {
    ServiceLoader.load(LuaConverter.class).forEach(this::getLuaConverter);
    ServiceLoader.load(LuaExtension.class).forEach(this::getLuaExtension);
  }

  public <C extends LuaConverter<?, ?>> C getLuaConverter(Class<C> converterClass) {
    C extension = converterExtensions.get(converterClass);
    if (extension == null) {
      extension = getExtension(converterClass);
      converterExtensions.add(extension);
      converters.addConverter(extension);
    }
    return extension;
  }

  @Override
  public <E extends LuaExtension> E getLuaExtension(Class<E> extensionClass) {
    E extension = luaExtensions.get(extensionClass);
    if (extension == null) {
      extension = getExtension(extensionClass);
      luaExtensions.add(extension);
      extension.installInto(env);
    }
    return extension;
  }

  private <E> E getExtension(Class<E> extensionClass) throws ServiceConfigurationError {
    E extension = extensions.get(extensionClass);
    if (extension == null) {
      extension = newInstance(extensionClass);
      extensions.add(extension);
      injector.injectServicesInto(extension);
    }
    return extension;
  }

  private static <P> P newInstance(Class<P> cls) throws ServiceConfigurationError {
    try {
      return cls.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      String message = "Provider " + cls + " could not be instantiated";
      throw new ServiceConfigurationError(LuaExtension.class.getName() + ": " + message, ex);
    }
  }
}
