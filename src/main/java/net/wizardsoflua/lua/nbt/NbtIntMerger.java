package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagInt;

public class NbtIntMerger implements NbtMerger<NBTTagInt> {
  @Override
  public NBTTagInt merge(NBTTagInt nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).intValue());
    }
    throw NbtMerger.conversionException(path, data, "Number");
  }
}
