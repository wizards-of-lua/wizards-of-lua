package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.ByteString;

public class NbtStringMerger implements NbtMerger<NBTTagString> {
  @Override
  public NBTTagString merge(NBTTagString nbt, Object data, String key, String path) {
    if (data instanceof ByteString) {
      return NbtConverter.toNbt((ByteString) data);
    }
    if (data instanceof String) {
      return NbtConverter.toNbt((String) data);
    }
    throw NbtMerger.conversionException(path, data, "String");
  }
}
