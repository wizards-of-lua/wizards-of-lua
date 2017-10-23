package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagFloat;

public class NbtFloatMerger implements NbtMerger<NBTTagFloat> {
  @Override
  public NBTTagFloat merge(NBTTagFloat nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).floatValue());
    }
    throw NbtMerger.conversionException(path, data, "Number");
  }
}
