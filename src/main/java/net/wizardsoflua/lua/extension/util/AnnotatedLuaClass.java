package net.wizardsoflua.lua.extension.util;

public abstract class AnnotatedLuaClass extends LuaClass {
  public static String getNameOf(Class<?> cls) {
    return cls.getAnnotation(LuaClassAttributes.class).name();
  }

  public static Class<? extends LuaClass> getSuperClassClassOf(Class<?> cls) {
    return cls.getAnnotation(LuaClassAttributes.class).superClass();
  }

  @Override
  public String getName() {
    return getNameOf(getClass());
  }

  @Override
  protected Class<? extends LuaClass> getSuperClassClass() {
    return getSuperClassClassOf(getClass());
  }
}
