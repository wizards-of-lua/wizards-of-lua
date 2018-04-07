package net.wizardsoflua.lua.module;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.spi.LuaModule;

public class LuaModuleLoader implements net.wizardsoflua.lua.extension.api.LuaModuleLoader {
  private final Map<Class<? extends LuaModule>, LuaModule> registeredModules = new HashMap<>();
  private final Map<Class<? extends LuaModule>, LuaModule> installedModules = new HashMap<>();
  private final Table env;
  private final InitializationContext initializationContext;

  public LuaModuleLoader(Table env, InitializationContext initializationContext) {
    this.env = checkNotNull(env, "env == null!");
    this.initializationContext =
        checkNotNull(initializationContext, "initializationContext == null!");
  }

  public void installModules() {
    ServiceLoader<LuaModule> modules = ServiceLoader.load(LuaModule.class);
    for (LuaModule module : modules) {
      register(module);
    }
    for (Class<? extends LuaModule> moduleType : registeredModules.keySet()) {
      getModule(moduleType);
    }
  }

  private <M extends LuaModule> M getRegisteredModule(Class<M> moduleType) {
    LuaModule module = registeredModules.get(moduleType);
    return moduleType.cast(module);
  }

  private void register(LuaModule module) {
    registeredModules.put(module.getClass(), module);
  }

  private <M extends LuaModule> M getInstalledModule(Class<M> moduleType) {
    LuaModule module = installedModules.get(moduleType);
    return moduleType.cast(module);
  }

  private <M extends LuaModule> M install(Class<M> moduleType) {
    M module = getRegisteredModule(moduleType);
    module.initialize(initializationContext);
    module.installInto(env);
    installedModules.put(module.getClass(), module);
    return module;
  }

  @Override
  public <M extends LuaModule> M getModule(Class<M> moduleType) {
    M module = getInstalledModule(moduleType);
    if (module == null) {
      return install(moduleType);
    }
    return module;
  }
}
