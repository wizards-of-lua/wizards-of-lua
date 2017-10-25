package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagInt;

public class NbtIntMerger implements NbtMerger<NBTTagInt> {
  private final NbtConverter converter;

  public NbtIntMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagInt merge(NBTTagInt nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).intValue());
    }
    throw converter.conversionException(path, data, "number");
  }
}
