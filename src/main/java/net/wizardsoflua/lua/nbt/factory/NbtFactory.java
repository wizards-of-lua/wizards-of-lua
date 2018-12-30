package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;

public interface NbtFactory<NBT extends NBTBase> {
  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<NbtFactory<?>> getClassWithWildcards() {
    return (Class) NbtFactory.class;
  }

  Class<NBT> getNbtClass();

  @Nullable
  NBT create(Object data, @Nullable NBT previousValue);

  String getLuaTypeName();
}
