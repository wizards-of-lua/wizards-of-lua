package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;

public interface NbtFactory<NBT extends NBTBase, D> {
  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<NbtFactory<?, ?>> getClassWithWildcards() {
    return (Class) NbtFactory.class;
  }

  Class<NBT> getNbtClass();

  Class<D> getDataClass();

  default @Nullable NBT tryCreate(Object data, @Nullable NBT previousValue) {
    Class<D> dataClass = getDataClass();
    if (dataClass.isInstance(data)) {
      return create(dataClass.cast(data), previousValue);
    } else {
      return null;
    }
  }

  @Nullable
  NBT create(D data, @Nullable NBT previous);
}
