package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public abstract class LuaClass {
  private final Table env;

  public LuaClass(Table env) {
    this.env = checkNotNull(env, "env == null!");
  }

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    String typeName = getModuleName();
    String module = getModule();
    String chunk = String.format("require \"%s\"", module);
    LuaFunction classFunc = loader.loadTextChunk(new Variable(env), typeName, chunk);
    executor.call(state, classFunc);
    Table luaClassTable = getLuaClassTable();
    checkNotNull(luaClassTable, "LuaClass %s was not created in module '%s'", typeName, module);
    addFunctions(luaClassTable);
  }

  public Table getLuaClassTable() {
    String typeName = getModuleName();
    return (Table) env.rawget(typeName);
  }

  protected abstract void addFunctions(Table luaClass);

  public String getModulePackage() {
    return getModulePackageOf(this.getClass());
  }

  public String getModuleName() {
    return getModuleNameOf(this.getClass());
  }

  public String getModule() {
    return getModulePackage() + "." + getModuleName();
  }

  public static String getModulePackageOf(Class<? extends LuaClass> cls) {
    return getLuaClassAnnotationOf(cls).packageName();
  }

  public static String getModuleNameOf(Class<? extends LuaClass> cls) {
    return getLuaClassAnnotationOf(cls).value();
  }

  private static LuaModule getLuaClassAnnotationOf(Class<? extends LuaClass> cls) {
    LuaModule luaClass = cls.getAnnotation(LuaModule.class);
    checkArgument(luaClass != null, "Class %s is not annotated with @%s", cls,
        LuaModule.class.getSimpleName());
    return luaClass;
  }
}
