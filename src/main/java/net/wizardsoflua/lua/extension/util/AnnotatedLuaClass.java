package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

public abstract class AnnotatedLuaClass extends LuaClass {
  private @Nullable String name;
  private @Nullable Class<? extends LuaClass> superClassClass;

  public static String getNameOf(Class<?> cls) {
    return cls.getAnnotation(LuaClassAttributes.class).name();
  }

  /**
   * Required unless @{@link LuaClassAttributes} is present, in which case
   * {@link LuaClassAttributes#name()} is used.
   */
  public static Class<? extends LuaClass> getSuperClassClassOf(Class<?> cls) {
    return cls.getAnnotation(LuaClassAttributes.class).superClass();
  }

  @Override
  public String getName() {
    if (name == null) {
      name = getNameOf(getClass());
    }
    return name;
  }

  @Override
  protected Class<? extends LuaClass> getSuperClassClass() {
    if (superClassClass == null) {
      superClassClass = getSuperClassClassOf(getClass());
    }
    return superClassClass;
  }
}
