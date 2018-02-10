package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.CustomLuaClass;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public class Types {
  private static final String NIL_META = "nil";
  private static final String BOOLEAN_META = "boolean";
  private static final String FUNCTION_META = "function";
  private static final String NUMBER_META = "number";
  private static final String STRING_META = "string";
  private static final String TABLE_META = "table";

  private final LuaClassLoader classLoader;

  public Types(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
  }

  /**
   * Declares a new {@link LuaClass} with the specified name and the optional superclass meta table.
   *
   * @param luaClassName
   * @param superClassMetaTable
   */
  public void declareClass(String luaClassName, @Nullable Table superClassMetaTable) {
    requireNonNull(luaClassName, "luaClassName == null!");
    Table env = classLoader.getEnv();
    checkState(env.rawget(luaClassName) == null,
        "bad argument #%s: a global variable with name '%s' is already defined", 1, luaClassName);

    LuaClass superClass;
    if (superClassMetaTable != null) {
      superClass = classLoader.getLuaClassForMetaTable(superClassMetaTable);
      checkArgument(superClass != null,
          "The table '%s' does not represent a LuaClass loaded by this LuaClassLoader",
          superClassMetaTable);
    } else {
      superClass = classLoader.getLuaClassOfType(ObjectClass.class);
    }
    classLoader.load(new CustomLuaClass(luaClassName, superClass));
  }

  /**
   * Returns {@code true} if the specified Lua object is an instance of the Lua class represented by
   * the specified class meta table.
   *
   * @param classMetaTable
   * @param luaObject
   * @return {@code true} if the specified Lua object is an instance of the Lua class
   */
  public boolean isInstanceOf(Table classMetaTable, Object luaObject) {
    if (luaObject == null) {
      return false;
    }
    if (!(luaObject instanceof Table)) {
      return false;
    }
    Table actualMetaTable = ((Table) luaObject).getMetatable();
    return actualMetaTable != null
        && (actualMetaTable == classMetaTable || isInstanceOf(classMetaTable, actualMetaTable));
  }

  /**
   * Returns the Lua type name of the given Lua object.
   *
   * @param luaObject
   * @return the Lua type name of the given Lua object
   */
  public @Nullable String getTypename(@Nullable Object luaObject) {
    if (luaObject == null) {
      return NIL_META;
    }
    if (luaObject instanceof Table) {
      Table table = (Table) luaObject;
      String classname = getClassname(table);
      if (classname != null) {
        return classname;
      }
    }
    return getTypename(luaObject.getClass());
  }

  public @Nullable String getTypename(Class<?> type) {
    if (DelegatingProxy.class.isAssignableFrom(type)) {
      @SuppressWarnings("unchecked")
      Class<? extends DelegatingProxy<?>> proxyClass = (Class<? extends DelegatingProxy<?>>) type;
      type = DelegatingProxy.getDelegateClassOf(proxyClass);
    }
    JavaLuaClass<?, ?> luaClass = classLoader.getLuaClassForJavaClass(type);
    if (luaClass != null) {
      return luaClass.getName();
    }
    if (Table.class.isAssignableFrom(type)) {
      return TABLE_META;
    }
    if (ByteString.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)) {
      return STRING_META;
    }
    if (Number.class.isAssignableFrom(type)) {
      return NUMBER_META;
    }
    if (Boolean.class.isAssignableFrom(type)) {
      return BOOLEAN_META;
    }
    if (LuaFunction.class.isAssignableFrom(type)) {
      return FUNCTION_META;
    }
    return type.getName();
  }

  /**
   * Returns the name of the {@link LuaClass} of the specified {@link Table} or {@code null} if the
   * {@link Table} is not an instance of a {@link LuaClass}.
   *
   * @param table
   * @return the name of the {@link LuaClass} of the specified {@link Table} or {@code null}
   */
  public @Nullable String getClassname(Table table) {
    Table metatable = table.getMetatable();
    if (metatable != null) {
      LuaClass luaClass = classLoader.getLuaClassForMetaTable(metatable);
      if (luaClass != null) {
        return luaClass.getName();
      }
    }
    return null;
  }
}
