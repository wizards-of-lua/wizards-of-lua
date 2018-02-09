package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.CustomLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.ObjectClass;

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

  public Table getEnv() {
    return classLoader.getEnv();
  }

  /**
   * Declares a new {@link LuaClass} with the specified name and the optional superclass meta table.
   *
   * @param luaClassName
   * @param superClassMetaTable
   */
  public void declareClass(String luaClassName, @Nullable Table superClassMetaTable) {
    requireNonNull(luaClassName, "luaClassName == null!");
    Table _G = classLoader.get_G();
    checkState(_G.rawget(luaClassName) == null,
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
   * Returns <code>true</code> if the given Lua object is an instance of the Lua class represented
   * by the given class metatable.
   *
   * @param classMT
   * @param luaObj
   * @return <code>true</code> if the given Lua object is an instance of the Lua class represented
   *         by the given class metatable
   */
  public boolean isInstanceOf(Table classMT, Object luaObj) {
    if (luaObj == null) {
      return false;
    }
    if (!(luaObj instanceof Table)) {
      return false;
    }
    Table actualMT = ((Table) luaObj).getMetatable();
    return actualMT != null && (actualMT == classMT || isInstanceOf(classMT, actualMT));
  }

  /**
   * Returns the Lua type name of the given Lua object.
   *
   * @param luaObj
   * @return the Lua type name of the given Lua object
   */
  public @Nullable String getTypename(@Nullable Object luaObj) {
    if (luaObj == null) {
      return NIL_META;
    }
    if (luaObj instanceof Table) {
      Table table = (Table) luaObj;
      String classname = getClassname(table);
      if (classname != null) {
        return classname;
      }
      return TABLE_META;
    }
    if ((luaObj instanceof ByteString) || (luaObj instanceof String)) {
      return STRING_META;
    }
    if (luaObj instanceof Number) {
      return NUMBER_META;
    }
    if (luaObj instanceof Boolean) {
      return BOOLEAN_META;
    }
    if (luaObj instanceof LuaFunction) {
      return FUNCTION_META;
    }
    return luaObj.getClass().getName();
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

  public void checkAssignable(String expectedMetatableName, Object luaObj)
      throws IllegalArgumentException {
    checkAssignable(expectedMetatableName, luaObj, Terms.MANDATORY);
  }

  public void checkAssignable(String expectedMetatableName, Object luaObj, Terms terms)
      throws IllegalArgumentException {
    if (luaObj == null && terms == Terms.MANDATORY
        || !isAssignable(expectedMetatableName, luaObj)) {
      throw new IllegalArgumentException(
          String.format("Expected %s but got %s", expectedMetatableName, getTypename(luaObj)));
    }
  }

  private boolean isAssignable(String expectedMetatableName, Object luaObj) {
    if (luaObj == null) {
      return true;
    }
    if (BOOLEAN_META.equals(expectedMetatableName)) {
      return luaObj instanceof Boolean;
    }
    if (NUMBER_META.equals(expectedMetatableName)) {
      return Conversions.numericalValueOf(luaObj) != null;
    }
    if (STRING_META.equals(expectedMetatableName)) {
      return Conversions.stringValueOf(luaObj) != null;
    }
    if (FUNCTION_META.equals(expectedMetatableName)) {
      return luaObj instanceof LuaFunction;
    }
    if (TABLE_META.equals(expectedMetatableName)) {
      return (luaObj instanceof Table);
    }
    if (luaObj instanceof Table) {
      Table actualMetatable = ((Table) luaObj).getMetatable();
      if (actualMetatable != null) {
        return isAssignable(expectedMetatableName, actualMetatable);
      }
    }
    return false;
  }

  private boolean isAssignable(String expectedMetatableName, Table actualMetatable) {
    Table expectedMetatable = (Table) getEnv().rawget(expectedMetatableName);
    Table currentMetatable = actualMetatable;
    while (currentMetatable != null) {
      if (currentMetatable == expectedMetatable) {
        return true;
      }
      currentMetatable = currentMetatable.getMetatable();
    }
    return false;
  }

}
