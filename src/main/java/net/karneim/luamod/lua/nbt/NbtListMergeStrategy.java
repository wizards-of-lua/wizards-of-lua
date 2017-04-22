package net.karneim.luamod.lua.nbt;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

public interface NbtListMergeStrategy {
  NBTTagList merge(NBTTagList origTagList, Table data);
}