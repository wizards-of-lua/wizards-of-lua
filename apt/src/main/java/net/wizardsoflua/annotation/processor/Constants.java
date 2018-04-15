package net.wizardsoflua.annotation.processor;

import static net.wizardsoflua.annotation.processor.Utils.getQualifiedName;

import com.squareup.javapoet.ClassName;

public interface Constants {
  final ClassName LUA_CONVERTERS_CLASS =
      ClassName.get("net.wizardsoflua.lua.extension.api.service", "LuaConverters");
  final String LUA_CONVERTER = "net.wizardsoflua.lua.extension.spi.LuaConverter";

  final String LUA_CLASS_API = "net.wizardsoflua.lua.classes.LuaClassApi";
  final ClassName OBJECT_CLASS = ClassName.get("net.wizardsoflua.lua.classes", "ObjectClass");
  final String JAVA_LUA_CLASS = "net.wizardsoflua.lua.classes.JavaLuaClass";
  final ClassName DECLARE_LUA_CLASS =
      ClassName.get("net.wizardsoflua.lua.classes", "DeclareLuaClass");
  final String DECLARE_LUA_CLASS_NAME = getQualifiedName(DECLARE_LUA_CLASS);

  final String TABLE_SUFFIX = "Table";
  final String MODULE_SUFFIX = "Module";
  final String CLASS_SUFFIX = "Class";
  final String INSTANCE_SUFFIX = "Instance";

  final ClassName LUA_TABLE_SUPERCLASS =
      ClassName.get("net.wizardsoflua.lua.table", "GeneratedLuaTable");
  final ClassName LUA_MODULE_SUPERCLASS =
      ClassName.get("net.wizardsoflua.lua.module", "GeneratedLuaModule");
  final ClassName LUA_CLASS_SUPERCLASS =
      ClassName.get("net.wizardsoflua.lua.classes", "InstanceCachingLuaClass");
  final ClassName LUA_INSTANCE_SUPERCLASS =
      ClassName.get("net.wizardsoflua.lua.classes", "GeneratedLuaInstance");

  static ClassName getNamedFunctionClassName(int numberOfArgs) {
    return ClassName.get("net.wizardsoflua.lua.function", "NamedFunction" + numberOfArgs);
  }
}
