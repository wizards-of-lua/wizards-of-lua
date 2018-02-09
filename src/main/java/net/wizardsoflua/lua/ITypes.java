package net.wizardsoflua.lua;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public interface ITypes {
  void checkAssignable(String expectedMetatableName, Object luaObj) throws IllegalArgumentException;

  @Nullable
  Table getClassMetatable(String classname);

  String getClassname(Table luaObj);

  String getTypename(Object luaObj);
}
