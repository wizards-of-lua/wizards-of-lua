package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

public interface NbtListMergeStrategy {
  NBTTagList merge(NBTTagList nbt, Table data, String path);
}
