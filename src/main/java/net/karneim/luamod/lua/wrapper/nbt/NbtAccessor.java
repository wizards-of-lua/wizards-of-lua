package net.karneim.luamod.lua.wrapper.nbt;

import net.minecraft.nbt.NBTBase;

public interface NbtAccessor<NBT extends NBTBase> {
  NBT getTag();

  void setTag(NBT tag);
}
