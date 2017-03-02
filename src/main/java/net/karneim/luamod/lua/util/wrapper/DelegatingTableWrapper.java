package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.EntityMetaTables;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;

public abstract class DelegatingTableWrapper<J> extends LuaWrapper<J, DelegatingTable> {
  public DelegatingTableWrapper(Table env, @Nullable J delegate) {
    super(env, delegate);
  }

  @Override
  protected final DelegatingTable toLuaObject() {
    DelegatingTable.Builder builder = DelegatingTable.builder(delegate);
    Class<?> cls = delegate.getClass();
    String name = EntityMetaTables.getName(cls);
    if ( name != null) {
      builder.add("classname", name);
      Table metatable = EntityMetaTables.getMetaTable(env, cls);
      builder.setMetatable(metatable);
    }
    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(DelegatingTable.Builder builder);
}
