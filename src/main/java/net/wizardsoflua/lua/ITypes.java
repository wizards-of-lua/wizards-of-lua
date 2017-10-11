package net.wizardsoflua.lua;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public interface ITypes {
  public Table declare(String classname, @Nullable String superclassname);

  public Table declare(String classname);

  public void checkAssignable(String expectedMetatableName, Object luaObj)
      throws IllegalArgumentException;

}
