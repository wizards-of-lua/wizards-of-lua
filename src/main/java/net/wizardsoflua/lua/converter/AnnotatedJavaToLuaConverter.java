package net.wizardsoflua.lua.converter;

import javax.annotation.Nullable;

import net.wizardsoflua.lua.classes.LuaClassAttributes;

public abstract class AnnotatedJavaToLuaConverter<J> extends TypeTokenJavaToLuaConverter<J> {
  private @Nullable String name;

  public static String getNameOf(Class<?> cls) {
    return cls.getAnnotation(LuaClassAttributes.class).name();
  }

  @Override
  public String getName() {
    if (name == null) {
      name = getNameOf(getClass());
    }
    return name;
  }
}
