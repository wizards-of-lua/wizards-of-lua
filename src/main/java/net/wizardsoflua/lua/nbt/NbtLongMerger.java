package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagLong;

public class NbtLongMerger implements NbtMerger<NBTTagLong> {
  @Override
  public NBTTagLong merge(NBTTagLong nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).longValue());
    }
    throw NbtMerger.conversionException(path, data, "Number");
  }
}
