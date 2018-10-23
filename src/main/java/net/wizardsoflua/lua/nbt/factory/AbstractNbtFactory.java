package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.minecraft.nbt.NBTBase;

public abstract class AbstractNbtFactory<NBT extends NBTBase, D> implements NbtFactory<NBT, D> {
  private @Nullable Class<NBT> nbtClass;

  @Override
  public Class<NBT> getNbtClass() {
    if (nbtClass == null) {
      TypeToken<NBT> token = new TypeToken<NBT>(getClass()) {
        private static final long serialVersionUID = 1L;
      };
      @SuppressWarnings("unchecked")
      Class<NBT> result = (Class<NBT>) token.getRawType();
      nbtClass = result;
    }
    return nbtClass;
  }

  private @Nullable Class<D> dataClass;

  @Override
  public Class<D> getDataClass() {
    if (dataClass == null) {
      TypeToken<D> token = new TypeToken<D>(getClass()) {
        private static final long serialVersionUID = 1L;
      };
      @SuppressWarnings("unchecked")
      Class<D> result = (Class<D>) token.getRawType();
      dataClass = result;
    }
    return dataClass;
  }
}
