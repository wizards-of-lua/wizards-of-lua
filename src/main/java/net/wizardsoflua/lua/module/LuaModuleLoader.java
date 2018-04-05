package net.wizardsoflua.lua.module;

import java.util.ServiceLoader;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.spi.LuaModule;

public class LuaModuleLoader {
  public static void installModulesInto(Table env, InitializationContext context) {
    ServiceLoader<LuaModule> modules = ServiceLoader.load(LuaModule.class);
    for (LuaModule module : modules) {
      module.initialize(context);
      module.installInto(env);
    }
  }
}
