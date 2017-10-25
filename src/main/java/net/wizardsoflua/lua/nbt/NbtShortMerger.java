package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagShort;

public class NbtShortMerger implements NbtMerger<NBTTagShort> {
  private final NbtConverter converter;

  public NbtShortMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagShort merge(NBTTagShort nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).shortValue());
    }
    throw converter.conversionException(path, data, "number");
  }
}
