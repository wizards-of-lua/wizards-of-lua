package net.wizardsoflua.lua;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public interface ITypes {
  Table declare(String classname, @Nullable String superclassname);

  Table declare(String classname);

  void checkAssignable(String expectedMetatableName, Object luaObj) throws IllegalArgumentException;

  @Nullable
  Table getClassMetatable(String classname);

  String getClassname(Table table);

  String getTypename(Object luaObj);

}
