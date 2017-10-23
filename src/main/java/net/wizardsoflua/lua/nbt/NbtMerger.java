package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.ConversionException;

public interface NbtMerger<NBT extends NBTBase> {
  static ConversionException conversionException(String path, Object actual, String expected) {
    return new ConversionException("Can't convert " + path + "! " + expected + " expected, but got "
        + actual.getClass().getName());
  }

  NBT merge(NBT nbt, Object data, String key, String path);
}
