package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public class LuaExtensionLoader implements net.wizardsoflua.lua.extension.api.LuaExtensionLoader {
  private final Map<Class<? extends LuaExtension>, LuaExtension> registeredExtensions =
      new HashMap<>();
  private final Map<Class<? extends LuaExtension>, LuaExtension> installedExtensions =
      new HashMap<>();
  private final Table env;
  private final InitializationContext initializationContext;

  public LuaExtensionLoader(Table env, InitializationContext initializationContext) {
    this.env = checkNotNull(env, "env == null!");
    this.initializationContext =
        checkNotNull(initializationContext, "initializationContext == null!");
  }

  public void installLuaExtensions() {
    ServiceLoader<LuaExtension> extensions = ServiceLoader.load(LuaExtension.class);
    for (LuaExtension extension : extensions) {
      register(extension);
    }
    for (Class<? extends LuaExtension> extensionType : registeredExtensions.keySet()) {
      getLuaExtension(extensionType);
    }
  }

  private <E extends LuaExtension> E getRegisteredLuaExtension(Class<E> extensionType) {
    LuaExtension extension = registeredExtensions.get(extensionType);
    return extensionType.cast(extension);
  }

  private void register(LuaExtension extension) {
    registeredExtensions.put(extension.getClass(), extension);
  }

  private <E extends LuaExtension> E getInstalledLuaExtension(Class<E> extensionType) {
    LuaExtension extension = installedExtensions.get(extensionType);
    return extensionType.cast(extension);
  }

  private <E extends LuaExtension> E install(Class<E> extensionType) {
    E extension = getRegisteredLuaExtension(extensionType);
    extension.initialize(initializationContext);
    extension.installInto(env);
    installedExtensions.put(extension.getClass(), extension);
    return extension;
  }

  @Override
  public <E extends LuaExtension> E getLuaExtension(Class<E> extensionType) {
    E extension = getInstalledLuaExtension(extensionType);
    if (extension == null) {
      return install(extensionType);
    }
    return extension;
  }
}
