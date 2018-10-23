package net.wizardsoflua.lua.classes.nbt;

import java.util.Set;

import com.google.common.collect.Iterables;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public class CompoundNbtTable extends NbtTable<NBTTagCompound> {
  public CompoundNbtTable(NbtAccessor<NBTTagCompound> accessor, Table metatable,
      Injector injector) {
    super(accessor, metatable, injector);
  }

  @Override
  protected NBTBase getChild(NBTTagCompound parent, Object key) {
    key = Conversions.javaRepresentationOf(key);
    if (key instanceof String) {
      return parent.getTag((String) key);
    }
    return null;
  }

  @Override
  protected void setChild(NBTTagCompound nbt, Object key, NBTBase value) {
    String k = converters.toJava(String.class, key, "key");
    nbt.setTag(k, value);
  }

  @Override
  protected Iterable<ByteString> getKeys() {
    NBTTagCompound nbt = getNbt();
    Set<String> keys = nbt.getKeySet();
    return Iterables.transform(keys, ByteString::of);
  }
}
