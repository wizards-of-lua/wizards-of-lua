package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeString;

import javax.annotation.Nullable;

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
  protected final LuaTypesRepo repo;

  public LuaClass(LuaTypesRepo repo) {
    this.repo = checkNotNull(repo, "repo == null!");
  }

  public final Table getEnv() {
    return repo.getEnv();
  }

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    String typeName = getModuleName();
    String module = getModule();
    String chunk = String.format("require \"%s\"", module);
    LuaFunction classFunc = loader.loadTextChunk(new Variable(getEnv()), typeName, chunk);
    executor.call(state, classFunc);
    Table luaClassTable = getLuaClassTable();
    checkNotNull(luaClassTable, "LuaClass %s was not created in module '%s'", typeName, module);
    addFunctions(luaClassTable);
  }

  public Table getLuaClassTable() {
    String typeName = getModuleName();
    return checkType(getEnv().rawget(typeName), Table.class);
  }

  public @Nullable Table getSuperClassTable() {
    Table luaClassTable = getLuaClassTable();
    return luaClassTable.getMetatable();
  }

  public @Nullable LuaClass getSuperClass() {
    Table superClassTable = getSuperClassTable();
    if (superClassTable == null) {
      return null;
    }
    String superClassName = checkTypeString(superClassTable.rawget("__classname"));
    return repo.get(superClassName);
  }

  protected abstract void addFunctions(Table luaClass);

  public String getModulePackage() {
    return getModulePackageOf(this.getClass());
  }

  public String getModuleName() {
    return getModuleNameOf(this.getClass());
  }

  public String getModule() {
    return getModuleOf(this.getClass());
  }

  public static String getModulePackageOf(Class<? extends LuaClass> cls) {
    String packageName = getLuaClassAnnotationOf(cls).packageName();
    if (packageName.isEmpty()) {
      return cls.getPackage().getName();
    }
    return packageName;
  }

  public static String getModuleNameOf(Class<? extends LuaClass> cls) {
    return getLuaClassAnnotationOf(cls).value();
  }

  public static String getModuleOf(Class<? extends LuaClass> cls) {
    return getModulePackageOf(cls) + "." + getModuleNameOf(cls);
  }

  private static LuaModule getLuaClassAnnotationOf(Class<? extends LuaClass> cls) {
    LuaModule luaClass = cls.getAnnotation(LuaModule.class);
    checkArgument(luaClass != null, "Class %s is not annotated with @%s", cls,
        LuaModule.class.getSimpleName());
    return luaClass;
  }
}
