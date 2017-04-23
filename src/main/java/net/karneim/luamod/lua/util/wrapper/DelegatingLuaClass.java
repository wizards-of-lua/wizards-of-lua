package net.karneim.luamod.lua.util.wrapper;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.sandius.rembulan.Table;

public abstract class DelegatingLuaClass<D> extends LuaClass
    implements LuaWrapper<D, DelegatingTable<D>> {
  public DelegatingLuaClass(Table env) {
    super(env);
  }

  @Override
  public final DelegatingTable<D> toLuaObject(D delegate) {
    DelegatingTable.Builder<D> builder = DelegatingTable.builder(delegate);
    addProperties(builder, delegate);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }

  protected abstract void addProperties(DelegatingTable.Builder<D> builder, D delegate);
}
