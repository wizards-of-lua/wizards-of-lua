package net.wizardsoflua.lua.classes.nbt;

import java.util.Collections;
import javax.annotation.Nullable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public abstract class IndexedNbtTable<NBT extends NBTBase> extends NbtTable<NBT> {
  public IndexedNbtTable(NbtAccessor<NBT> accessor, Table metatable, Injector injector) {
    super(accessor, metatable, injector);
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
    int luaIndex = converters.toJava(int.class, key, "key");
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

  protected boolean isInRange(int javaIndex, NBT parent) {
    return 0 <= javaIndex && javaIndex < getSize(parent);
  }

  protected void checkJavaIndex(int javaIndex, NBT parent) {
    if (!isInRange(javaIndex, parent)) {
      int luaIndex = javaIndex + 1;
      throw new BadArgumentException("number (" + luaIndex + ") out of range 1, " + getSize(parent))
          .setArgumentName("key");
    }
  }
}
