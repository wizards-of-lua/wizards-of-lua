package net.wizardsoflua.lua.extension.api.service;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

public interface LuaClassLoader {
  // TODO Adrodoc 08.04.2018: Unify with LuaModuleLoader#getModule(Class)
  /**
   * Returns the {@link LuaClass} instance of the specified type, loading it if neccessary.
   *
   * @param luaClassClass
   * @return the {@link LuaClass} instance
   */
  <LC extends LuaClass> LC getLuaClassOfType(Class<LC> luaClassClass);

  /**
   * Returns the {@link LuaClass} with the specified table or {@code null} if no such
   * {@link LuaClass} was loaded by {@code this} {@link LuaClassLoader}.
   *
   * @param luaClassTable the table of the {@link LuaClass}
   * @return the {@link LuaClass} with the specified table or {@code null}
   * @throws NullPointerException if the specified table is {@code null}
   */
  LuaClass getLuaClassForClassTable(Table luaClassTable) throws NullPointerException;

  /**
   * Returns the {@link LuaClass} of the specified {@link Table}. If {@code luaObject} is not an
   * instance of a class then {@code null} is returned.
   *
   * @param luaObject
   * @return the {@link LuaClass} or {@code null}
   */
  LuaClass getLuaClassOfInstance(Table luaObject);

  <J> JavaLuaClass<J, ?> getLuaClassForJavaClass(Class<J> javaClass);

  void load(LuaClass luaClass);
}
