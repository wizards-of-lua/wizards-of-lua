package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ServiceConfigurationError;
import java.util.Set;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public class LuaExtensionLoader implements net.wizardsoflua.lua.extension.api.LuaExtensionLoader {
  private final ClassIndex extensions = new ClassIndex();
  private final Table env;
  private final InitializationContext initializationContext;

  public LuaExtensionLoader(Table env, InitializationContext initializationContext) {
    this.env = checkNotNull(env, "env == null!");
    this.initializationContext =
        checkNotNull(initializationContext, "initializationContext == null!");
  }

  public void installLuaExtensions() {
    Set<Class<? extends LuaExtension>> extensions = ServiceLoader.load(LuaExtension.class);
    for (Class<? extends LuaExtension> extension : extensions) {
      getLuaExtension(extension);
    }
  }

  @Override
  public <E extends LuaExtension> E getLuaExtension(Class<E> extensionType) {
    E extension = extensions.get(extensionType);
    if (extensionType == null) {
      extension = newInstance(extensionType);
      extensions.add(extension);
      extension.initialize(initializationContext);
      extension.installInto(env);
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
