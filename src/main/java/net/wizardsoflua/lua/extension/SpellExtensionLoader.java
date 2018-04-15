package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.util.ServiceConfigurationError;
import java.util.function.Consumer;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.extension.spi.ConverterExtension;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public class SpellExtensionLoader
    implements net.wizardsoflua.lua.extension.api.service.LuaExtensionLoader {
  private final ClassIndex extensions = new ClassIndex();
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
    ServiceLoader.load(LuaExtension.class).forEach(this::getLuaExtension);
    ServiceLoader.load(ConverterExtension.class).forEach(this::getConverterExtension);
  }

  @Override
  public <E extends LuaExtension> E getLuaExtension(Class<E> extensionClass) {
    return getExtension(extensionClass, extension -> {
      injector.injectServicesInto(extension);
      extension.installInto(env);
    });
  }

  public <E extends ConverterExtension<?, ?>> E getConverterExtension(Class<E> extensionClass) {
    return getExtension(extensionClass, extension -> {
      injector.injectServicesInto(extension);
      converters.addConverterExtension(extension);
    });
  }

  private <E> E getExtension(Class<E> extensionClass, Consumer<E> initializer)
      throws ServiceConfigurationError {
    E extension = extensions.get(extensionClass);
    if (extension == null) {
      extension = newInstance(extensionClass);
      extensions.add(extension);
      initializer.accept(extension);
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
