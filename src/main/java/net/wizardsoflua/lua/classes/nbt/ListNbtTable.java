package net.wizardsoflua.lua.classes.nbt;

import java.util.Collections;
import javax.annotation.Nullable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public class ListNbtTable extends IndexedNbtTable<NBTTagList> {
  public ListNbtTable(NbtAccessor<NBTTagList> accessor, Table metatable, Injector injector) {
    super(accessor, metatable, injector);
  }

  @Override
  protected @Nullable NBTBase getChild(NBTTagList parent, Object key) {
    Integer luaIndex = Converters.integerValueOf(key);
    if (luaIndex != null && 1 <= luaIndex && luaIndex <= parent.tagCount()) {
      return parent.get(luaIndex - 1);
    }
    return null;
  }

  @Override
  protected void setChild(NBTTagList nbt, Object key, NBTBase value) {
    int luaIndex = converters.toJava(int.class, key, "key");
    int javaIndex = luaIndex - 1;
    if (value == null) {
      if (0 <= javaIndex && javaIndex < nbt.tagCount()) {
        nbt.removeTag(javaIndex);
      }
    } else if (javaIndex == nbt.tagCount()) {
      nbt.appendTag(value);
    } else {
      nbt.set(javaIndex, value);
    }
  }

  @Override
  protected Iterable<Integer> getKeys() {
    NBTTagList nbt = getNbt();
    if (nbt.hasNoTags()) {
      return Collections.emptyList();
    }
    Range<Integer> closed = Range.closed(1, nbt.tagCount());
    return ContiguousSet.create(closed, DiscreteDomain.integers());
  }

  @Override
  protected int getSize(NBTTagList parent) {
    return parent.tagCount();
  }

  @Override
  protected NBTBase getChild(NBTTagList parent, int javaIndex) {
    return parent.get(javaIndex);
  }

  @Override
  protected void setChild(NBTTagList parent, int javaIndex, @Nullable NBTBase value) {
    if (value == null) {
      if (isInRange(javaIndex, parent)) {
        parent.removeTag(javaIndex);
      }
    } else if (javaIndex == parent.tagCount()) {
      parent.appendTag(value);
    } else {
      checkJavaIndex(javaIndex, parent);
      parent.set(javaIndex, value);
    }
  }
}
