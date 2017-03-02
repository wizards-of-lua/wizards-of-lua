package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.ClassMetatables;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.sandius.rembulan.Table;

public abstract class ImmutableTableWrapper<J> extends LuaWrapper<J, PatchedImmutableTable> {
  public ImmutableTableWrapper(Table env, @Nullable J delegate) {
    super(env, delegate);
  }

  @Override
  protected final PatchedImmutableTable toLuaObject() {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();

    builder.setMetatable(ClassMetatables.getMetatable(env, delegate.getClass()));

    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(PatchedImmutableTable.Builder builder);
}
