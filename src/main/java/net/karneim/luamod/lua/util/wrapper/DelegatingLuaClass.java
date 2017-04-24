package net.karneim.luamod.lua.util.wrapper;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;

public abstract class DelegatingLuaClass<D> extends LuaClass
    implements LuaWrapper<D, DelegatingTable<D>> {
  public DelegatingLuaClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  public final DelegatingTable<D> toLuaObject(D delegate) {
    DelegatingTable.Builder<D> builder = DelegatingTable.builder(delegate);
    addAllProperties(builder, delegate);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }

  public final void addAllProperties(DelegatingTable.Builder<? extends D> builder, D delegate) {
    LuaClass superClass = getSuperClass();
    if (superClass != null && superClass instanceof DelegatingLuaClass) {
      @SuppressWarnings("unchecked")
      DelegatingLuaClass<? super D> uncheckedSuperClass =
          (DelegatingLuaClass<? super D>) superClass;
      uncheckedSuperClass.addAllProperties(builder, delegate);
    }
    addProperties(builder, delegate);
  }

  protected abstract void addProperties(DelegatingTable.Builder<? extends D> builder, D delegate);
}
