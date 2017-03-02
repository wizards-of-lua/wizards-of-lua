package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.ClassMetatables;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.sandius.rembulan.Table;

public abstract class DelegatingTableWrapper<J> extends LuaWrapper<J, DelegatingTable> {
  public DelegatingTableWrapper(Table env, @Nullable J delegate) {
    super(env, delegate);
  }

  @Override
  protected final DelegatingTable toLuaObject() {
    DelegatingTable.Builder builder = DelegatingTable.builder(delegate);
    
    builder.setMetatable(ClassMetatables.getMetatable(env, delegate.getClass()));
    
    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(DelegatingTable.Builder builder);
}
