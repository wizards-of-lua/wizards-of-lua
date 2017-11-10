package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagFloat;

public class NbtFloatMerger implements NbtMerger<NBTTagFloat> {
  private final NbtConverter converter;

  public NbtFloatMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagFloat merge(NBTTagFloat nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).floatValue());
    }
    throw converter.conversionException(path, data, "number");
  }
}
