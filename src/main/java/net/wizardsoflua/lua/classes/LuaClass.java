package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.common.Named;

public abstract class LuaClass implements Named {
  /**
   * The {@link LuaClassLoader} that loaded {@code this} {@link LuaClass}.
   */
  private @Nullable LuaClassLoader classLoader;
  /**
   * The metatable to use for instances and subclass tables. It contains all functions of
   * {@code this} {@link LuaClass}.
   */
  private final Table metaTable = new DefaultTable();

  public LuaClass() {
    metaTable.rawset("__index", metaTable);
  }

  /**
   * Called by the {@link LuaClassLoader} when this {@link LuaClass} is beeing loaded.
   *
   * @param classLoader
   */
  void load(LuaClassLoader classLoader) {
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    LuaClass superClass = getSuperClass();
    if (superClass != null) {
      metaTable.setMetatable(superClass.getMetaTable());
    }
    onLoad();
  }

  protected void onLoad() {}

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
  public Table getMetaTable() {
    return metaTable;
  }

  public abstract @Nullable LuaClass getSuperClass();

  public <F extends LuaFunction & Named> void add(F function) {
    add(function.getName(), function);
  }

  public void add(String name, LuaFunction function) {
    metaTable.rawset(name, function);
  }
}
