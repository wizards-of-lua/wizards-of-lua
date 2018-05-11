package net.wizardsoflua.extension.spell.api.resource;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public interface LuaTypes {
  static final String BOOLEAN = "boolean";
  static final String FUNCTION = "function";
  static final String NIL = "nil";
  static final String NUMBER = "number";
  static final String STRING = "string";
  static final String TABLE = "table";

  Table registerLuaClass(String className, Table classTable);

  String getLuaTypeName(@Nullable Object instance);

  @Nullable
  String getLuaClassName(Table instance);

  String getLuaTypeName(Class<?> javaClass) throws IllegalArgumentException;

  @Nullable
  Table getLuaClassTableForName(String luaClassName);
}
