package net.wizardsoflua.lua.nbt.accessor;

import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

public class NbtRootAccessor<NBT extends NBTBase> implements NbtAccessor<NBT> {
  private final INBTSerializable<NBT> serializable;

  public NbtRootAccessor(INBTSerializable<NBT> serializable) {
    this.serializable = requireNonNull(serializable, "serializable == null!");
  }

  @Override
  public String getNbtPath() {
    return "nbt";
  }

  @Override
  public NBT getNbt() {
    return serializable.serializeNBT();
  }

  @Override
  public void modifyNbt(Consumer<? super NBT> consumer) {
    NBT nbt = getNbt();
    consumer.accept(nbt);
    serializable.deserializeNBT(nbt);
  }
}
