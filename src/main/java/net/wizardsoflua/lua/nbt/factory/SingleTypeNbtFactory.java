package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.common.reflect.TypeToken;
import net.minecraft.nbt.NBTBase;

public abstract class SingleTypeNbtFactory<NBT extends NBTBase, D> extends AbstractNbtFactory<NBT> {
  private @Nullable Class<D> dataClass;

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

  @Override
  public String getLuaTypeName() {
    return getDataClass().getSimpleName().toLowerCase();
  }

  @Override
  public @Nullable NBT create(Object data, @Nullable NBT previousValue) {
    Class<D> dataClass = getDataClass();
    if (dataClass.isInstance(data)) {
      return createTypesafe(dataClass.cast(data), previousValue);
    } else {
      return null;
    }
  }

  protected abstract @Nullable NBT createTypesafe(D data, @Nullable NBT previous);
}
