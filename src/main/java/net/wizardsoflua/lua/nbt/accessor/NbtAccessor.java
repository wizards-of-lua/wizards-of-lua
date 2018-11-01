package net.wizardsoflua.lua.nbt.accessor;

import java.util.function.Consumer;

import net.minecraft.nbt.NBTBase;

public interface NbtAccessor<NBT extends NBTBase> {
  NBT getNbt();

  void modifyNbt(Consumer<? super NBT> consumer);
}
