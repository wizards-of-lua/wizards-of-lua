package net.karneim.luamod.lua.classes;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.TypeToken;

import net.karneim.luamod.lua.LuaUtil;
import net.karneim.luamod.lua.util.wrapper.CachingLuaClass;

public class GlobalLuaTypesRepo {
  private final List<Class<? extends LuaClass>> luaClasses = new ArrayList<>();
  private final Map<Class<?>, Class<? extends LuaClass>> javaToLuaClassMap = new HashMap<>();

  public GlobalLuaTypesRepo() {
    ClassPath cp;
    try {
      cp = ClassPath.from(LuaUtil.class.getClassLoader());
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
    ImmutableSet<ClassInfo> classes =
        cp.getTopLevelClassesRecursive("net.karneim.luamod.lua.classes");
    for (ClassInfo classInfo : classes) {
      Class<?> cls = classInfo.load();
      if (LuaClass.class.isAssignableFrom(cls) && cls.isAnnotationPresent(LuaModule.class)) {
        @SuppressWarnings("unchecked")
        Class<? extends LuaClass> luaClass = (Class<? extends LuaClass>) cls;
        luaClasses.add(luaClass);
        Class<?> javaClass = getJavaClass(luaClass);
        if ( javaClass != null) {
          javaToLuaClassMap.put(javaClass, luaClass);
        }
      }
    }
  }

  private Class<?> getJavaClass(Class<?> luaClass) {
    if (CachingLuaClass.class.isAssignableFrom(luaClass)) {
      return getTypeParameter(luaClass);
    }
    return null;
  }
  
  public static Class<?> getTypeParameter(Class<?> cls) {
    TypeToken<?> token = TypeToken.of(cls).resolveType(CachingLuaClass.class.getTypeParameters()[0]);
    return token.getRawType();
  }

  public List<Class<? extends LuaClass>> getLuaClasses() {
    return luaClasses;
  }

  @Nullable
  public <C extends Class<? extends LuaClass>> C getLuaClass(Class<?> javaClass) {
    Class<? extends LuaClass> result = javaToLuaClassMap.get(javaClass);
    if (result != null) {
      return (C) result;
    }
    // TODO should we scan superclass & superinterfaces?
    return null;
  }
}
