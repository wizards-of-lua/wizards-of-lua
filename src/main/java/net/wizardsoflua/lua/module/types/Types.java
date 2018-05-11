package net.wizardsoflua.lua.module.types;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.Delegator;

public class Types implements LuaTypes {
  private final LuaClassLoader classLoader;
  private final LuaConverters converters;
  private final BiMap<String, Table> classes = HashBiMap.create();

  public Types(LuaClassLoader classLoader, @Resource LuaConverters converters) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    this.converters = requireNonNull(converters, "converters == null!");
  }

  @Override
  public @Nullable Table getLuaClassTableForName(String luaClassName) {
    requireNonNull(luaClassName, "luaClassName == null!");
    return classes.get(luaClassName);
  }

  @Override
  public Table registerLuaClass(String className, Table classTable) {
    requireNonNull(className, "className == null!");
    requireNonNull(classTable, "classTable == null!");
    return classes.put(className, classTable);
  }

  @Override
  public String getLuaTypeName(@Nullable Object instance) {
    if (instance == null) {
      return NIL;
    }
    if (instance instanceof Table) {
      Table instanceTable = (Table) instance;
      String result = getLuaClassName(instanceTable);
      if (result != null) {
        return result;
      }
    }
    return getLuaTypeName(instance.getClass());
  }

  @Override
  public @Nullable String getLuaClassName(Table instance) {
    requireNonNull(instance, "instance == null!");
    BiMap<Table, String> inverse = classes.inverse();
    if (inverse.containsKey(instance)) {
      return "class";
    }
    Table metatable = instance.getMetatable();
    return inverse.get(metatable);
  }

  @Override
  public String getLuaTypeName(Class<?> javaClass) throws IllegalArgumentException {
    LuaConverter<?, ?> converter = converters.getLuaConverterForJavaClass(javaClass);
    if (converter != null) {
      return converter.getName();
    }
    String legacyResult = getLegacyLuaTypeName(javaClass);
    if (legacyResult != null) {
      return legacyResult;
    }
    if (Table.class.isAssignableFrom(javaClass)) {
      return TABLE;
    }
    if (ByteString.class.isAssignableFrom(javaClass) || String.class.isAssignableFrom(javaClass)) {
      return STRING;
    }
    if (Number.class.isAssignableFrom(javaClass)) {
      return NUMBER;
    }
    if (Boolean.class.isAssignableFrom(javaClass)) {
      return BOOLEAN;
    }
    if (LuaFunction.class.isAssignableFrom(javaClass)) {
      return FUNCTION;
    }
    throw new IllegalArgumentException("Unknown lua type: " + javaClass.getName());
  }

  // TODO Adrodoc 11.05.2018: Remove this when we got rid of LuaClassApi
  @Deprecated
  private String getLegacyLuaTypeName(Class<?> javaClass) {
    if (Delegator.class.isAssignableFrom(javaClass)) {
      javaClass = unproxy(javaClass);
    }
    JavaLuaClass<?, ?> luaClass = classLoader.getLuaClassForJavaClass(javaClass);
    if (luaClass != null) {
      return luaClass.getName();
    }
    return null;
  }

  @Deprecated
  private <T> Class<T> unproxy(Class<?> type) {
    @SuppressWarnings("unchecked")
    Class<? extends Delegator<T>> delegatorClass = (Class<? extends Delegator<T>>) type;
    return Delegator.getDelegateClassOf(delegatorClass);
  }
}
