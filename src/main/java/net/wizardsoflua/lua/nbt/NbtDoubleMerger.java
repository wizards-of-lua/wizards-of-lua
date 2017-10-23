package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagDouble;

public class NbtDoubleMerger implements NbtMerger<NBTTagDouble> {
  @Override
  public NBTTagDouble merge(NBTTagDouble nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).doubleValue());
    }
    throw NbtMerger.conversionException(path, data, "Number");
  }
}
