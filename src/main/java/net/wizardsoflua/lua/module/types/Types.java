package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.ITypes;
import net.wizardsoflua.lua.classes.CustomLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public class Types implements ITypes {
  private static final String NIL_META = "nil";
  private static final String BOOLEAN_META = "boolean";
  private static final String NUMBER_META = "number";
  private static final String STRING_META = "string";
  private static final String TABLE_META = "table";
  private static final String FUNCTION_META = "function";
  private static final String CLASSNAME_META_KEY = "__classname";

  private final LuaClassLoader luaClassLoader;
  private final Map<Table, String> classes = new HashMap<Table, String>();

  public Types(LuaClassLoader luaClassLoader) {
    this.luaClassLoader = requireNonNull(luaClassLoader, "luaClassLoader == null!");
  }

  public Table getEnv() {
    return luaClassLoader.getEnv();
  }

  public Table get_G() {
    return luaClassLoader.get_G();
  }

  /**
   * Returns the metatable of the Lua class with the given classname, of <code>null</code>, if no
   * such class has been declared.
   *
   * @param classname
   * @return the metatable of the Lua class with the given classname
   */
  @Override
  public @Nullable Table getClassMetatable(String classname) {
    checkNotNull(classname, "classname==null!");
    Table _G = get_G();
    Object value = _G.rawget(classname);
    if (value instanceof Table) {
      Table result = (Table) value;
      String declaredClassname = classes.get(result);
      if (classname.equals(declaredClassname)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Declares a new Lua class with the given name and the optionally given superclass meta table.
   *
   * @param luaClassName
   * @param superClassMetaTable
   */
  public void declareClass(String luaClassName, @Nullable Table superClassMetaTable) {
    requireNonNull(luaClassName, "luaClassName == null!");
    checkState(get_G().rawget(luaClassName) == null,
        "bad argument #%s: a global variable with name '%s' is already defined", 1, luaClassName);

    LuaClass superClass;
    if (superClassMetaTable != null) {
      superClass = luaClassLoader.getLuaClassForMetaTable(superClassMetaTable);
    } else {
      superClass = luaClassLoader.getObjectClass();
    }
    luaClassLoader.load(new CustomLuaClass(luaClassName, superClass));
  }

  /**
   * Returns the classname of the given Lua object, or <code>null</code> if the object is not an
   * instance belonging to a class.
   *
   * @param luaObj
   * @return the classname
   */
  @Override
  public @Nullable String getClassname(Table luaObj) {
    if (isMetatable(luaObj)) {
      return null;
    }
    Table mt = luaObj.getMetatable();
    if (mt != null) {
      return classes.get(mt);
    }
    return null;
  }

  private boolean isMetatable(Table table) {
    return classes.containsKey(table);
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
   * Returns the Lua type name of the given Lua object, of <code>null</code>, it the name is
   * unknown.
   *
   * @param luaObj
   * @return the Lua type name of the given Lua object
   */
  @Override
  public @Nullable String getTypename(@Nullable Object luaObj) {
    if (luaObj == null) {
      return NIL_META;
    }
    if (luaObj instanceof Boolean) {
      return BOOLEAN_META;
    }
    if (luaObj instanceof Number) {
      return NUMBER_META;
    }
    if (luaObj instanceof LuaFunction) {
      return FUNCTION_META;
    }
    if ((luaObj instanceof ByteString) || (luaObj instanceof String)) {
      return STRING_META;
    }
    if (luaObj instanceof Table) {
      Table table = (Table) luaObj;
      String classname = getClassname(table);
      if (classname != null) {
        return classname;
      }
      return TABLE_META;
    }
    return null;
  }

  @Override
  public void checkAssignable(String expectedMetatableName, Object luaObj)
      throws IllegalArgumentException {
    checkAssignable(expectedMetatableName, luaObj, Terms.MANDATORY);
  }

  public void checkAssignable(String expectedMetatableName, Object luaObj, Terms terms)
      throws IllegalArgumentException {
    if (luaObj == null && terms == Terms.MANDATORY
        || !isAssignable(expectedMetatableName, luaObj)) {
      throw new IllegalArgumentException(
          String.format("Expected %s but got %s", expectedMetatableName, getTypeOf(luaObj)));
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

  private String getTypeOf(Object luaObj) {
    if (luaObj == null) {
      return NIL_META;
    }
    if (luaObj instanceof Table) {
      Table actualMetatable = ((Table) luaObj).getMetatable();
      if (actualMetatable == null) {
        return TABLE_META;
      }
      Object actualTypeName = actualMetatable.rawget(CLASSNAME_META_KEY);
      if (actualTypeName != null) {
        return String.valueOf(actualTypeName);
      } else {
        return TABLE_META;
      }
    }
    if (luaObj instanceof LuaFunction) {
      return FUNCTION_META;
    }
    if (luaObj instanceof Number) {
      return NUMBER_META;
    }
    if (luaObj instanceof Boolean) {
      return BOOLEAN_META;
    }
    if (luaObj instanceof ByteString || luaObj instanceof String) {
      return STRING_META;
    }
    // Fallback
    return luaObj.getClass().getSimpleName();
  }

}
