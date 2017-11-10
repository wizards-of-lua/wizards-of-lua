package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagDouble;

public class NbtDoubleMerger implements NbtMerger<NBTTagDouble> {
  private final NbtConverter converter;

  public NbtDoubleMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagDouble merge(NBTTagDouble nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).doubleValue());
    }
    throw converter.conversionException(path, data, "number");
  }
}
