package net.wizardsoflua.lua.classes.entity;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public class EntityNbtAccessor implements NbtAccessor<NBTTagCompound> {
  private final Entity entity;

  public EntityNbtAccessor(Entity entity) {
    this.entity = requireNonNull(entity, "entity == null!");
  }

  @Override
  public boolean isAttached() {
    return true;
  }

  @Override
  public NBTTagCompound getNbt() {
    return entity.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void modifyNbt(Consumer<? super NBTTagCompound> consumer) {
    NBTTagCompound nbt = getNbt();
    consumer.accept(nbt);
    entity.readFromNBT(nbt);
  }
}
