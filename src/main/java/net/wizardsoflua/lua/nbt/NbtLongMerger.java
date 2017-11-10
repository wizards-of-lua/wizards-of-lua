package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagLong;

public class NbtLongMerger implements NbtMerger<NBTTagLong> {
  private final NbtConverter converter;

  public NbtLongMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagLong merge(NBTTagLong nbt, Object data, String key, String path) {
    if (data instanceof Number) {
      return NbtConverter.toNbt(((Number) data).longValue());
    }
    throw converter.conversionException(path, data, "number");
  }
}
