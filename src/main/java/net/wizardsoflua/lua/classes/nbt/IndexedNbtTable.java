package net.wizardsoflua.lua.classes.nbt;

import java.util.Collections;
import javax.annotation.Nullable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;
import net.wizardsoflua.lua.nbt.accessor.NbtChildAccessor;

public abstract class IndexedNbtTable<NBT extends NBTBase> extends NbtTable<NBT> {
  public IndexedNbtTable(NbtAccessor<NBT> accessor, Table metatable, Injector injector) {
    super(accessor, metatable, injector);
  }

  @Override
  protected <C extends NBTBase> NbtChildAccessor<C, NBT> getChildAccessor(
      Class<C> expectedChildType, Object key) {
    return new NbtChildAccessor<C, NBT>(expectedChildType, accessor) {
      @Override
      public String getNbtPath() {
        return IndexedNbtTable.this.getNbtPath(key);
      }

      @Override
      protected NBTBase getChildRaw(NBT parentNbt) {
        return getChild(parentNbt, key);
      }
    };
  }

  protected String getNbtPath(Object key) {
    return accessor.getNbtPath() + '[' + key + ']';
  }

  @Override
  protected @Nullable NBTBase getChild(NBT parent, Object key) {
    Integer luaIndex = Converters.integerValueOf(key);
    if (luaIndex != null && 1 <= luaIndex && luaIndex <= getSize(parent)) {
      int javaIndex = luaIndex - 1;
      return getChild(parent, javaIndex);
    }
    return null;
  }

  @Override
  protected void setChild(NBT parent, Object key, @Nullable NBTBase child) {
    Integer luaIndex = Converters.integerValueOf(key);
    if (luaIndex == null) {
      throw new IllegalOperationAttemptException("bad key (" + key + ") for table '"
          + accessor.getNbtPath() + "': key must be an integer");
    }
    int javaIndex = luaIndex - 1;
    setChild(parent, javaIndex, child);
  }

  @Override
  protected Iterable<Integer> getKeys() {
    NBT nbt = getNbt();
    if (nbt.hasNoTags()) {
      return Collections.emptyList();
    }
    Range<Integer> closed = Range.closed(1, getSize(nbt));
    return ContiguousSet.create(closed, DiscreteDomain.integers());
  }

  protected abstract int getSize(NBT parent);

  protected abstract NBTBase getChild(NBT parent, int javaIndex);

  protected abstract void setChild(NBT parent, int javaIndex, @Nullable NBTBase value);

  protected boolean isInRange(int javaIndex, int range) {
    return 0 <= javaIndex && javaIndex < range;
  }

  protected void checkJavaIndex(int javaIndex, int range) {
    if (!isInRange(javaIndex, range)) {
      int luaIndex = javaIndex + 1;
      throw new LuaRuntimeException("bad key (" + luaIndex + ") for table '" + accessor.getNbtPath()
          + "': index out of range [1;" + range + "]");
    }
  }
}
