package net.karneim.luamod.lua.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;

public class FixedSizeCollectionWrapper<J, L, D>
    implements LuaWrapper<FixedSizeCollection<J, D>, DelegatingTable<D>> {
  private final Class<L> luaType;
  private final Function<J, L> toLua;
  private final Function<L, J> toJava;

  public FixedSizeCollectionWrapper(Class<L> luaType, Function<J, L> toLua, Function<L, J> toJava) {
    this.luaType = checkNotNull(luaType, "luaType == null!");
    this.toLua = checkNotNull(toLua, "toLua == null!");
    this.toJava = checkNotNull(toJava, "toJava == null!");
  }

  @Override
  public DelegatingTable<D> createLuaObject(FixedSizeCollection<J, D> javaObject) {
    Builder<D> b = DelegatingTable.builder(javaObject.getDelegate());
    addProperties(b, javaObject);
    return b.build();
  }

  public void addProperties(Builder<? extends D> b, FixedSizeCollection<J, D> javaObject) {
    for (int i = 0; i < javaObject.getLength(); i++) {
      final int luaIndex = i + 1;
      Supplier<L> get = () -> toLua.apply(javaObject.getAt(luaIndex - 1));
      Consumer<Object> set =
          l -> javaObject.setAt(luaIndex - 1, toJava.apply(checkType(l, luaType)));
      b.add(luaIndex, get, set);
    }
  }
}
