package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

public class ObjectClass extends LuaClass {
  @Override
  public String getName() {
    return "Object";
  }

  @Override
  public @Nullable LuaClass getSuperClass() {
    return null;
  }
}
