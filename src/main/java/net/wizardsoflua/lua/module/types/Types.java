package net.wizardsoflua.lua.module.types;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.lua.Converters;

@SpellScoped
public class Types implements LuaTypes {
  private final Provider<Converters> convertersProvider;
  private final BiMap<String, Table> classes = HashBiMap.create();

  public Types(Provider<Converters> convertersProvider) {
    this.convertersProvider = requireNonNull(convertersProvider, "convertersProvider == null!");
  }

  public Converters getConverters() {
    return convertersProvider.get();
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
  public String getLuaTypeNameOfLuaObject(@Nullable Object luaObject) {
    if (luaObject == null) {
      return NIL;
    }
    if (luaObject instanceof Table) {
      Table instanceTable = (Table) luaObject;
      String result = getLuaClassNameOfLuaObject(instanceTable);
      if (result != null) {
        return result;
      }
    }
    return getLuaTypeName(luaObject.getClass());
  }

  @Override
  public @Nullable String getLuaClassNameOfLuaObject(Table luaObject) {
    requireNonNull(luaObject, "luaObject == null!");
    BiMap<Table, String> inverse = classes.inverse();
    if (inverse.containsKey(luaObject)) {
      return "class";
    }
    Table metatable = luaObject.getMetatable();
    return inverse.get(metatable);
  }

  public String getLuaTypeNameForJavaClass(Class<?> javaClass) throws IllegalArgumentException {
    LuaToJavaConverter<?, ?> converter = getConverters().getLuaToJavaConverter(javaClass);
    if (converter != null) {
      return converter.getName();
    }
    return getLuaTypeName(javaClass);
  }

  private String getLuaTypeName(Class<?> cls) {
    if (Table.class.isAssignableFrom(cls)) {
      return TABLE;
    }
    if (ByteString.class.isAssignableFrom(cls) || String.class.isAssignableFrom(cls)) {
      return STRING;
    }
    if (Number.class.isAssignableFrom(cls)) {
      return NUMBER;
    }
    if (Boolean.class.isAssignableFrom(cls)) {
      return BOOLEAN;
    }
    if (LuaFunction.class.isAssignableFrom(cls)) {
      return FUNCTION;
    }
    throw new IllegalArgumentException("Unknown lua type: " + cls.getName());
  }
}
