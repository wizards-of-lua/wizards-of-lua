package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.sandius.rembulan.Table;

public abstract class DelegatingTableWrapper<J> extends LuaWrapper<J, DelegatingTable> {
  private final Table metatable;

  public DelegatingTableWrapper(Table env, @Nullable J delegate, Table metatable) {
    super(env, delegate);
    this.metatable = metatable;
  }

  @Override
  protected final DelegatingTable toLuaObject() {
    DelegatingTable.Builder builder = DelegatingTable.builder(delegate);
    addProperties(builder);
    builder.setMetatable(metatable);
    return builder.build();
  }

  protected abstract void addProperties(DelegatingTable.Builder builder);
  
  public static <T> T getDelegate(Class<T> cls, Object arg) {
    if ( arg instanceof DelegatingTable) {
      DelegatingTable tbl = (DelegatingTable)arg;
      Object delegate = tbl.getDelegate();
      if ( cls.isInstance(delegate)) {
        return (T)delegate;
      }
    }
    return null;
  }
}
