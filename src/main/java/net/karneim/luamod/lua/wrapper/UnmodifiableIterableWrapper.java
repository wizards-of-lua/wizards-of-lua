package net.karneim.luamod.lua.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;

public class UnmodifiableIterableWrapper<J, L>
    implements LuaWrapper<Iterable<J>, DelegatingTable<Iterable<J>>> {
  private final Function<J, L> toLua;

  public UnmodifiableIterableWrapper(Function<J, L> toLua) {
    this.toLua = checkNotNull(toLua, "toLua == null!");
  }

  @Override
  public DelegatingTable<Iterable<J>> createLuaObject(Iterable<J> iterable) {
    Builder<Iterable<J>> b = DelegatingTable.builder(iterable);
    int i = 0;
    for (J javaObject : iterable) {
      final int luaIndex = i + 1;
      b.addReadOnly(luaIndex, () -> toLua.apply(javaObject));
    }
    return b.build();
  }
}
