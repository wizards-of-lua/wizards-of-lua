package net.karneim.luamod.lua.classes;

public interface LuaType {

  public default String getTypeName() {
    return typeNameOf(this.getClass());
  }

  public static String typeNameOf(Class<? extends LuaType> type) {
    LuaClass luaClass = type.getAnnotation(LuaClass.class);
    if (luaClass != null) {
      return luaClass.value();
    }
    throw new IllegalArgumentException(
        String.format("Class %s has no @%s", type, LuaClass.class.getSimpleName()));
  }

  public default String getModulePackage() {
    return modulePackageOf(this.getClass());
  }

  public static String modulePackageOf(Class<? extends LuaType> type) {
    LuaClass luaClass = type.getAnnotation(LuaClass.class);
    if (luaClass != null) {
      return luaClass.packageName();
    }
    throw new IllegalArgumentException(
        String.format("Class %s has no @%s", type, LuaClass.class.getSimpleName()));
  }

  public default String getModule() {
    return getModulePackage() + "." + getTypeName();
  }

  public void setRepo(LuaTypesRepo repo);

  public LuaTypesRepo getRepo();

}
