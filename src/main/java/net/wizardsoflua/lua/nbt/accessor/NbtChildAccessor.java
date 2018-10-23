package net.wizardsoflua.lua.nbt.accessor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.nbt.NBTBase;

public class NbtChildAccessor<NBT extends NBTBase, P extends NBTBase> implements NbtAccessor<NBT> {
  private final Class<NBT> expectedType;
  private NbtAccessor<P> parent;
  private final Function<? super P, ? extends NBTBase> getChild;
  private NBT snapshot;

  public NbtChildAccessor(Class<NBT> expectedType, NbtAccessor<P> parent,
      Function<? super P, ? extends NBTBase> getChild) {
    this.expectedType = checkNotNull(expectedType, "expectedType == null!");
    this.parent = checkNotNull(parent, "parent == null!");
    this.getChild = checkNotNull(getChild, "getChild == null!");
    refreshSnapshot();
  }

  @Override
  public boolean isAttached() {
    return parent != null;
  }

  @Override
  public NBT getNbt() {
    refreshSnapshot();
    return snapshot;
  }

  @Override
  public void modifyNbt(Consumer<? super NBT> consumer) {
    if (parent != null) {
      parent.modifyNbt(parentNbt -> {
        refreshSnapshot(parentNbt);
        consumer.accept(snapshot);
      });
    } else {
      consumer.accept(snapshot);
    }
  }

  private void refreshSnapshot() {
    if (parent != null) {
      refreshSnapshot(parent.getNbt());
    }
  }

  private void refreshSnapshot(P parentNbt) {
    NBTBase nbt = getChild.apply(parentNbt);
    if (expectedType.isInstance(nbt)) {
      snapshot = expectedType.cast(nbt);
    } else {
      parent = null;
    }
  }
}
