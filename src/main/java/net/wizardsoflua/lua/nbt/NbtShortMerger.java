package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagShort;

public class NbtShortMerger implements NbtMerger<NBTTagShort> {
  @Override
  public NBTTagShort merge(NBTTagShort nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).shortValue());
    }
    throw NbtMerger.conversionException(path, data, "Number");
  }
}
