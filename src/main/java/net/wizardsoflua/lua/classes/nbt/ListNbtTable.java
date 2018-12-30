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
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public class ListNbtTable extends IndexedNbtTable<NBTTagList> {
  public ListNbtTable(NbtAccessor<NBTTagList> accessor, Table metatable, Injector injector) {
    super(accessor, metatable, injector);
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
    checkJavaIndex(javaIndex, parent.tagCount() + 1);
    if (value == null) {
      if (isInRange(javaIndex, parent.tagCount())) {
        parent.removeTag(javaIndex);
      }
    } else {
      if (value.getId() != parent.getTagType()) {
        String expected = nbtConverters.getLuaTypeName(parent.getTagType());
        String actual = nbtConverters.getLuaTypeName(value.getId());
        int luaIndex = javaIndex + 1;
        throw new BadArgumentException(expected, actual)
            .setFunctionOrPropertyName(getNbtPath(luaIndex));
      }
      if (javaIndex == parent.tagCount()) {
        parent.appendTag(value);
      } else {
        parent.set(javaIndex, value);
      }
    }
  }
}
