package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.EntityMetaTables;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.minecraft.entity.EntityList;
import net.sandius.rembulan.Table;

public abstract class ImmutableTableWrapper<J> extends LuaWrapper<J, PatchedImmutableTable> {
  public ImmutableTableWrapper(Table env, @Nullable J delegate) {
    super(env, delegate);
  }

  @Override
  protected final PatchedImmutableTable toLuaObject() {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    
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

  protected abstract void addProperties(PatchedImmutableTable.Builder builder);
}
