package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.INBTBase;

public interface NbtMerger<NBT extends INBTBase> {
  NBT merge(NBT nbt, Object data, String key, String path);
}
