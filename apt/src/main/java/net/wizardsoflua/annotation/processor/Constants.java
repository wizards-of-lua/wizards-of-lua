package net.wizardsoflua.annotation.processor;

import static net.wizardsoflua.annotation.processor.Utils.getQualifiedName;

import com.squareup.javapoet.ClassName;

public interface Constants {
  public static final String OBJECT_CLASS = "net.wizardsoflua.lua.classes.ObjectClass";
  public static final String JAVA_LUA_CLASS = "net.wizardsoflua.lua.classes.JavaLuaClass";
  public static final ClassName DECLARE_LUA_CLASS_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "DeclareLuaClass");
  public static final String DECLARE_LUA_CLASS = getQualifiedName(DECLARE_LUA_CLASS_CLASS_NAME);
  public static final ClassName LUA_MODULE_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "LuaModule");
  public static final ClassName LUA_CLASS_LOADER_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "LuaClassLoader");
  public static final ClassName EXECUTION_CONTEXT_CLASS_NAME =
      ClassName.get("net.sandius.rembulan.runtime", "ExecutionContext");
  public static final ClassName RESOLVED_CONTROL_THROWABLE_CLASS_NAME =
      ClassName.get("net.sandius.rembulan.runtime", "ResolvedControlThrowable");
  public static final ClassName PROXY_CACHING_LUA_CLASS_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "ProxyCachingLuaClass");

  public static ClassName getNamedFunctionClassName(int numberOfArgs) {
    return ClassName.get("net.wizardsoflua.lua.function", "NamedFunction" + numberOfArgs);
  }
}
