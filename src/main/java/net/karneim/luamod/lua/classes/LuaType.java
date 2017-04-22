package net.karneim.luamod.lua.classes;

public interface LuaType {

  public default String getTypeName() {
    return typeNameOf(this.getClass());
  }

  public static String typeNameOf(Class<? extends LuaType> type) {
    TypeName a = type.getAnnotation(TypeName.class);
    if (a != null) {
      return a.value();
    }
    throw new IllegalArgumentException(
        String.format("Class %s has no %s", type, TypeName.class.getSimpleName()));
  }

  public default String getModulePackage() {
    return modulePackageOf(this.getClass());
  }

  public static String modulePackageOf(Class<? extends LuaType> type) {
    ModulePackage a = type.getAnnotation(ModulePackage.class);
    if (a != null) {
      return a.value();
    }
    throw new IllegalArgumentException(
        String.format("Class %s has no %s", type, ModulePackage.class.getSimpleName()));
  }

  public default String getModule() {
    return getModulePackage() + "." + getTypeName();
  }

  public void setRepo(LuaTypesRepo repo);

  public LuaTypesRepo getRepo();

}
