package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.sandius.rembulan.Table;

public abstract class ImmutableTableWrapper<J> extends LuaWrapper<J, PatchedImmutableTable> {
  private final Table metatable;

  public ImmutableTableWrapper(LuaTypesRepo repo, @Nullable J delegate, Table metatable) {
    super(repo, delegate);
    this.metatable = metatable;
  }

  @Override
  protected final PatchedImmutableTable toLuaObject() {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();

    addProperties(builder);
    builder.setMetatable(metatable);

    return builder.build();
  }

  protected abstract void addProperties(PatchedImmutableTable.Builder builder);
}
