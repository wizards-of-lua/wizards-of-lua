package net.wizardsoflua.lua.classes.nbt;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;
import net.wizardsoflua.lua.nbt.accessor.NbtChildAccessor;

public class CompoundNbtTable extends NbtTable<NBTTagCompound> {
  public CompoundNbtTable(NbtAccessor<NBTTagCompound> accessor, Table metatable,
      Injector injector) {
    super(accessor, metatable, injector);
  }

  @Override
  protected <C extends NBTBase> NbtChildAccessor<C, NBTTagCompound> getChildAccessor(
      Class<C> expectedChildType, Object key) {
    return new NbtChildAccessor<C, NBTTagCompound>(expectedChildType, accessor) {
      @Override
      public String getNbtPath() {
        return CompoundNbtTable.this.getNbtPath(key);
      }

      @Override
      protected NBTBase getChildRaw(NBTTagCompound parentNbt) {
        return getChild(parentNbt, key);
      }
    };
  }

  @Override
  protected String getNbtPath(Object key) {
    return accessor.getNbtPath() + '.' + key;
  }

  @Override
  protected @Nullable NBTBase getChild(NBTTagCompound parent, Object key) {
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
