package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.common.Named;

public abstract class LuaClass {
  /**
   * The {@link LuaClassLoader} that loaded {@code this} {@link LuaClass}.
   */
  private LuaClassLoader classLoader;
  /**
   * The metatable to use for instances and subclass tables. It contains all functions of
   * {@code this} {@link LuaClass}.
   */
  private Table metaTable = new DefaultTable();
  private final Map<String, LuaFunction> functions = new HashMap<>();

  // Pseudo constructor. This way you don't have to overwrite the constructor in every subclass
  protected void init(LuaClassLoader classLoader) {
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    metaTable.rawset("__index", metaTable);
    for (Map.Entry<String, LuaFunction> e : functions.entrySet()) {
      metaTable.rawset(e.getKey(), e.getValue());
    }
    LuaClass superClass = getSuperClass();
    if (superClass != null) {
      metaTable.setMetatable(superClass.getMetaTable());
    }
  }

  /**
   * Returns the value of {@link #classLoader}.
   *
   * @return the value of {@link #classLoader}
   * @throws IllegalStateException if {@code this} {@link LuaClass} is not initialized yet
   */
  public LuaClassLoader getClassLoader() throws IllegalStateException {
    checkState(classLoader != null, "LuaClass '%s' is not initialized yet", getName());
    return classLoader;
  }

  /**
   * Returns the value of {@link #metaTable}.
   *
   * @return the value of {@link #metaTable}
   */
  public Table getMetaTable() throws IllegalStateException {
    return metaTable;
  }

  public abstract String getName();

  public abstract @Nullable LuaClass getSuperClass();

  protected <F extends LuaFunction & Named> void add(F function) {
    add(function.getName(), function);
  }

  protected void add(String name, LuaFunction function) {
    functions.put(name, function);
  }
}
