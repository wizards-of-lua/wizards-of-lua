package net.karneim.luamod.lua.util.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;

public abstract class DelegatingLuaClass<D> extends LuaClass
    implements LuaWrapper<D, DelegatingTable<D>> {
  protected final LuaTypesRepo repo;

  public DelegatingLuaClass(LuaTypesRepo repo) {
    super(repo.getEnv());
    this.repo = checkNotNull(repo, "repo == null!");
  }

  protected abstract void addProperties(DelegatingTable.Builder<D> builder, D delegate);

  @Override
  public final DelegatingTable<D> toLuaObject(D delegate) {
    DelegatingTable.Builder<D> builder = DelegatingTable.builder(delegate);
    addProperties(builder, delegate);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }
}
