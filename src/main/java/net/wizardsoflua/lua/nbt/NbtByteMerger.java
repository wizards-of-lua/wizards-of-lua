package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagByte;

public class NbtByteMerger implements NbtMerger<NBTTagByte> {
  @Override
  public NBTTagByte merge(NBTTagByte nbt, Object data, String key, String path) {
    if (data instanceof Boolean) {
      return NbtConverter.toNbt((Boolean) data);
    }
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).byteValue());
    }
    throw NbtMerger.conversionException(path, data, "Boolean or Number");
  }
}
