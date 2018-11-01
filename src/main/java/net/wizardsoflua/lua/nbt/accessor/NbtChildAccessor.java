package net.wizardsoflua.lua.nbt.accessor;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;

public class NbtChildAccessor<NBT extends NBTBase, P extends NBTBase> implements NbtAccessor<NBT> {
  private final Class<NBT> expectedType;
  private final NbtAccessor<P> parent;
  private final Function<? super P, ? extends NBTBase> getChild;

  public NbtChildAccessor(Class<NBT> expectedType, NbtAccessor<P> parent,
      Function<? super P, ? extends NBTBase> getChild) {
    this.expectedType = checkNotNull(expectedType, "expectedType == null!");
    this.parent = checkNotNull(parent, "parent == null!");
    this.getChild = checkNotNull(getChild, "getChild == null!");
  }

  @Override
  public NBT getNbt() {
    P parentNbt = parent.getNbt();
    return getChild(parentNbt);
  }

  @Override
  public void modifyNbt(Consumer<? super NBT> consumer) {
    parent.modifyNbt(parentNbt -> {
      NBT nbt = getChild(parentNbt);
      consumer.accept(nbt);
    });
  }

  private NBT getChild(P parentNbt) {
    NBTBase nbt = getChild.apply(parentNbt);
    if (expectedType.isInstance(nbt)) {
      return expectedType.cast(nbt);
    } else {
      throw new IllegalOperationAttemptException("attempt to access invalid nbt table");
    }
  }
}
