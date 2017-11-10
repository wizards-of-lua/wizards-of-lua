package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;

public class NbtCompoundMerger implements NbtMerger<NBTTagCompound> {
  private final NbtConverter converter;

  public NbtCompoundMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagCompound merge(NBTTagCompound nbt, Object data, String key, String path) {
    if (data instanceof Table) {
      return converter.merge(nbt, (Table) data, path);
    }
    throw converter.conversionException(path, data, "table");
  }
}
