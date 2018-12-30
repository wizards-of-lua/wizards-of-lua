package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.common.reflect.TypeToken;
import net.minecraft.nbt.NBTBase;

public abstract class AbstractNbtFactory<NBT extends NBTBase> implements NbtFactory<NBT> {
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
}
