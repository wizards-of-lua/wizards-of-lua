package net.wizardsoflua.lua.nbt.accessor;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.LuaRuntimeException;

public abstract class NbtChildAccessor<NBT extends NBTBase, P extends NBTBase>
    implements NbtAccessor<NBT> {
  private final Class<NBT> expectedType;
  private final NbtAccessor<P> parent;

  public NbtChildAccessor(Class<NBT> expectedType, NbtAccessor<P> parent) {
    this.expectedType = checkNotNull(expectedType, "expectedType == null!");
    this.parent = checkNotNull(parent, "parent == null!");
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
    NBTBase nbt = getChildRaw(parentNbt);
    if (expectedType.isInstance(nbt)) {
      return expectedType.cast(nbt);
    } else {
      throw new LuaRuntimeException("the nbt table '" + getNbtPath() + "' changed its type to "
          + NBTBase.getTagTypeName(nbt.getId()));
    }
  }

  protected abstract NBTBase getChildRaw(P parentNbt);
}
