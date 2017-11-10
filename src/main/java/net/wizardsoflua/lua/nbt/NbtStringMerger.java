package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.ByteString;

public class NbtStringMerger implements NbtMerger<NBTTagString> {
  private final NbtConverter converter;

  public NbtStringMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagString merge(NBTTagString nbt, Object data, String key, String path) {
    if (data instanceof ByteString) {
      return NbtConverter.toNbt((ByteString) data);
    }
    if (data instanceof String) {
      return NbtConverter.toNbt((String) data);
    }
    throw converter.conversionException(path, data, "string");
  }
}
