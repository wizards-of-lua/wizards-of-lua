package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTBase;

public interface NbtMerger<NBT extends NBTBase> {
  NBT merge(NBT nbt, Object data, String key, String path);
}
