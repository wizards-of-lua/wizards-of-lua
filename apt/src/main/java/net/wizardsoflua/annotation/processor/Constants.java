package net.wizardsoflua.annotation.processor;

import static net.wizardsoflua.annotation.processor.Utils.getQualifiedName;

import com.squareup.javapoet.ClassName;

public interface Constants {
  static final String PROXY_SUFFIX = "Proxy";
  static final String CLASS_SUFFIX = "Class";
  static final String MODULE_SUFFIX = "Module";
  static final ClassName OBJECT_CLASS_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "ObjectClass");
  static final String OBJECT_CLASS = getQualifiedName(OBJECT_CLASS_CLASS_NAME);
  static final String JAVA_LUA_CLASS = "net.wizardsoflua.lua.classes.JavaLuaClass";
  static final ClassName DECLARE_LUA_CLASS_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "DeclareLuaClass");
  static final String DECLARE_LUA_CLASS = getQualifiedName(DECLARE_LUA_CLASS_CLASS_NAME);
  static final ClassName LUA_MODULE_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "LuaModule");
  static final ClassName LUA_CLASS_LOADER_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "LuaClassLoader");
  static final ClassName EXECUTION_CONTEXT_CLASS_NAME =
      ClassName.get("net.sandius.rembulan.runtime", "ExecutionContext");
  static final ClassName RESOLVED_CONTROL_THROWABLE_CLASS_NAME =
      ClassName.get("net.sandius.rembulan.runtime", "ResolvedControlThrowable");
  static final ClassName PROXY_CACHING_LUA_CLASS_CLASS_NAME =
      ClassName.get("net.wizardsoflua.lua.classes", "ProxyCachingLuaClass");
  static final ClassName LUA_API_PROXY_CLASS_NAME =
      ClassName.get("net.wizardsoflua.scribble", "LuaApiProxy");
  static final String LUA_API_BASE = "net.wizardsoflua.scribble.LuaApiBase";

  static ClassName getNamedFunctionClassName(int numberOfArgs) {
    return ClassName.get("net.wizardsoflua.lua.function", "NamedFunction" + numberOfArgs);
  }
}
