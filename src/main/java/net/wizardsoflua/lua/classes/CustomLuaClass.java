package net.wizardsoflua.lua.classes;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

public class CustomLuaClass extends LuaClass {
  private String name;
  private @Nullable LuaClass superClass;

  public CustomLuaClass(String name, @Nullable LuaClass superClass) {
    this.name = requireNonNull(name, "name == null!");
    this.superClass = superClass;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public @Nullable LuaClass getSuperClass() {
    return superClass;
  }
}
