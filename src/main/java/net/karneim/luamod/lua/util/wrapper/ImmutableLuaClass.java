package net.karneim.luamod.lua.util.wrapper;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;

public abstract class ImmutableLuaClass<J> extends LuaClass
    implements LuaWrapper<J, PatchedImmutableTable> {
  public ImmutableLuaClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  public final PatchedImmutableTable toLuaObject(J javaObject) {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    addAllProperties(builder, javaObject);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }

  public final void addAllProperties(PatchedImmutableTable.Builder builder, J javaObject) {
    LuaClass superClass = getSuperClass();
    if (superClass != null && superClass instanceof ImmutableLuaClass) {
      @SuppressWarnings("unchecked")
      ImmutableLuaClass<? super J> uncheckedSuperClass = (ImmutableLuaClass<? super J>) superClass;
      uncheckedSuperClass.addAllProperties(builder, javaObject);
    }
    addProperties(builder, javaObject);
  }

  protected abstract void addProperties(PatchedImmutableTable.Builder builder, J javaObject);
}
