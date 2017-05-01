package net.karneim.luamod.lua.wrapper;

import java.util.function.Function;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;

public class ModifiableArrayWrapper<J, L> implements LuaWrapper<J[], DelegatingTable<J[]>> {
  private final FixedSizeCollectionWrapper<J, L, J[]> fixedSizeCollectionWrapper;

  public ModifiableArrayWrapper(Class<L> luaType, Function<J, L> toLua, Function<L, J> toJava) {
    fixedSizeCollectionWrapper = new FixedSizeCollectionWrapper<J, L, J[]>(luaType, toLua, toJava);
  }

  @Override
  public DelegatingTable<J[]> createLuaObject(J[] javaObject) {
    return fixedSizeCollectionWrapper.createLuaObject(new FixedSizeCollection<J, J[]>() {
      @Override
      public J getAt(int i) {
        return javaObject[i];
      }

      @Override
      public void setAt(int i, J element) {
        javaObject[i] = element;
      }

      @Override
      public int getLength() {
        return javaObject.length;
      }

      @Override
      public J[] getDelegate() {
        return javaObject;
      }
    });
  }
}
