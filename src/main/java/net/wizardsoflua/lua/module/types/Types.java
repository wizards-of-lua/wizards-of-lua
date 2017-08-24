package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.LuaFunction;

public class Types {
  private static final String NIL_META = "nil";
  private static final String BOOLEAN_META = "boolean";
  private static final String NUMBER_META = "number";
  private static final String STRING_META = "string";
  private static final String TABLE_META = "table";
  private static final String FUNCTION_META = "function";

  private static final String CLASSNAME_META_KEY = "__classname";
  private final Table env;
  private final Map<Table, String> classes = new HashMap<Table, String>();

  public Types(Table env) {
    this.env = env;
  }

  public Table getEnv() {
    return env;
  }

  /**
   * Returns the metatable of the Lua class with the given classname, of <code>null</code>, if no
   * such class has been declared.
   * 
   * @param classname
   * @return the metatable of the Lua class with the given classname
   */
  public @Nullable Table getClassMetatable(String classname) {
    checkNotNull(classname, "classname==null!");
    Table G = (Table) env.rawget("_G");
    Object value = G.rawget(classname);
    if (value instanceof Table) {
      Table result = (Table) value;
      String declaredClassname = classes.get(result);
      if (classname.equals(declaredClassname)) {
        return result;
      }
    }
    return null;
  }

  public Table declare(String classname) {
    return declare(classname, (Table)null);
  }
  
  public Table declare(String classname, @Nullable String superclassname) {
    Table superclassMT = null;
    if ( superclassname!=null) {
      superclassMT = getClassMetatable(superclassname);
    }
    return declare(classname, superclassMT);
  }
  
  /**
   * Declares a new Lua class with the given name and the optionally given superclass metatable.
   * 
   * @param classname
   * @param superclassMT
   * @return the new class metatable
   */
  public Table declare(String classname, @Nullable Table superclassMT) {
    checkNotNull(classname, "classname==null!");
    Table G = (Table) env.rawget("_G");
    checkNotNull(G, "G==null!");
    boolean classnameAvailable = G.rawget(classname) == null;
    checkState(classnameAvailable, String.format(
        "bad argument #%s (a global variable with name '%s' is already defined)", 1, classname));

    Table classMT = new DefaultTable();
    classMT.rawset("__index", classMT);
    classMT.rawset(CLASSNAME_META_KEY, classname);
    classMT.setMetatable(superclassMT);

    classes.put(classMT, classname);

    G.rawset(classname, classMT);
    return classMT;
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
      Table mt = ((Table) luaObj).getMetatable();
      if (mt != null) {
        String classname = classes.get(mt);
        if (classname != null) {
          return classname;
        }
      }
      return TABLE_META;
    }
    return null;
  }

  public void checkBoolean(Object luaObj) {
    checkAssignable(BOOLEAN_META, luaObj);
  }

  public void checkBoolean(Object luaObj, Terms terms) {
    checkAssignable(BOOLEAN_META, luaObj, terms);
  }

  public Boolean castBoolean(Object luaObj, Terms terms) {
    checkAssignable(BOOLEAN_META, luaObj, terms);
    return (Boolean) luaObj;
  }

  public void checkNumber(Object luaObj) {
    checkAssignable(NUMBER_META, luaObj);
  }

  public void checkNumber(Object luaObj, Terms terms) {
    checkAssignable(NUMBER_META, luaObj, terms);
  }

  public Number castNumber(Object luaObj, Terms terms) {
    checkAssignable(NUMBER_META, luaObj, terms);
    return Conversions.numericalValueOf(luaObj);
  }

  public void checkString(Object luaObj) {
    checkAssignable(STRING_META, luaObj);
  }

  public void checkString(Object luaObj, Terms terms) {
    checkAssignable(STRING_META, luaObj, terms);
  }

  public String castString(Object luaObj, Terms terms) {
    checkAssignable(STRING_META, luaObj, terms);
    return luaObj == null ? null : String.valueOf(luaObj);
  }

  public void checkTable(Object luaObj) {
    checkAssignable(TABLE_META, luaObj);
  }

  public void checkTable(Object luaObj, Terms terms) {
    checkAssignable(TABLE_META, luaObj, terms);
  }

  public Table castTable(Object luaObj, Terms terms) {
    checkAssignable(TABLE_META, luaObj, terms);
    return (Table) luaObj;
  }

  public void checkFunction(Object luaObj) {
    checkAssignable(FUNCTION_META, luaObj);
  }

  public void checkFunction(Object luaObj, Terms terms) {
    checkAssignable(FUNCTION_META, luaObj, terms);
  }

  public LuaFunction castFunction(Object luaObj, Terms terms) {
    checkAssignable(FUNCTION_META, luaObj, terms);
    return (LuaFunction) luaObj;
  }

  public void checkAssignable(String expectedMetatableName, Object luaObj) {
    checkAssignable(expectedMetatableName, luaObj, Terms.OPTIONAL);
  }

  public void checkAssignable(String expectedMetatableName, Object luaObj, Terms terms) {
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
    Table expectedMetatable = (Table) env.rawget(expectedMetatableName);
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
