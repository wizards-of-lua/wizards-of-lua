package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagByte;

public class NbtByteMerger implements NbtMerger<NBTTagByte> {
  private final NbtConverter converter;

  public NbtByteMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagByte merge(NBTTagByte nbt, Object data, String key, String path) {
    if (data instanceof Boolean) {
      return NbtConverter.toNbt((Boolean) data);
    }
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).byteValue());
    }
    throw converter.conversionException(path, data, "boolean/number");
  }
}
