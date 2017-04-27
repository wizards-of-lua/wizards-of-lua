package net.karneim.luamod.lua.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;

public class ModifiableArrayWrapper<J, L> implements LuaWrapper<J[], DelegatingTable<J[]>> {
  private final Class<L> luaType;
  private final Function<J, L> toLua;
  private final Function<L, J> toJava;

  public ModifiableArrayWrapper(Class<L> luaType, Function<J, L> toLua,
      Function<L, J> toJava) {
    this.luaType = checkNotNull(luaType, "luaType == null!");
    this.toLua = checkNotNull(toLua, "toLua == null!");
    this.toJava = checkNotNull(toJava, "toJava == null!");
  }

  @Override
  public DelegatingTable<J[]> createLuaObject(J[] javaObject) {
    Builder<J[]> b = DelegatingTable.builder(javaObject);
    for (int i = 0; i < javaObject.length; i++) {
      final int luaIndex = i + 1;
      Supplier<L> get = () -> toLua.apply(javaObject[luaIndex - 1]);
      Consumer<Object> set = l -> javaObject[luaIndex - 1] = toJava.apply(checkType(l, luaType));
      b.add(luaIndex, get, set);
    }
    return b.build();
  }
}
