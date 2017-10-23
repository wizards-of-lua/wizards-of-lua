package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;

public class NbtCompoundMerger implements NbtMerger<NBTTagCompound> {
  @Override
  public NBTTagCompound merge(NBTTagCompound nbt, Object data, String key, String path) {
    if (data instanceof Table) {
      return NbtConverter.merge(nbt, (Table) data, path);
    }
    throw NbtMerger.conversionException(path, data, "Table");
  }
}
