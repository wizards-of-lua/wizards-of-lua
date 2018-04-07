package net.wizardsoflua.lua.extension.api;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;

public interface InitializationContext {
  LuaClassLoader getClassLoader();

  Config getConfig();

  Converter getConverter();

  Table getEnv();

  ExceptionHandler getExceptionHandler();

  LuaExecutor getLuaExecutor();

  LuaModuleLoader getModuleLoader();

  Spell getSpell();

  TableFactory getTableFactory();

  Time getTime();
}
