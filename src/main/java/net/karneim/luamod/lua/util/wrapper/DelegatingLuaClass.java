package net.karneim.luamod.lua.util.wrapper;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;

public abstract class DelegatingLuaClass<D> extends LuaClass
    implements LuaWrapper<D, DelegatingTable<D>> {
  public DelegatingLuaClass(LuaTypesRepo repo) {
    super(repo);
  }

  protected abstract void addProperties(DelegatingTable.Builder<? extends D> builder, D delegate);

  @Override
  public final DelegatingTable<D> toLuaObject(D delegate) {
    DelegatingTable.Builder<D> builder = DelegatingTable.builder(delegate);
    LuaClass superClass = getSuperClass();
    if (superClass != null && superClass instanceof DelegatingLuaClass) {
      @SuppressWarnings("unchecked")
      DelegatingLuaClass<? super D> uncheckedSuperClass =
          (DelegatingLuaClass<? super D>) superClass;
      uncheckedSuperClass.addProperties(builder, delegate);
    }
    addProperties(builder, delegate);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }
}
