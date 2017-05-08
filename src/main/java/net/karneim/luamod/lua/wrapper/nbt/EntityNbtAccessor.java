package net.karneim.luamod.lua.wrapper.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class EntityNbtAccessor implements NbtAccessor<NBTTagCompound> {
  private final Entity entity;

  public EntityNbtAccessor(Entity entity) {
    this.entity = checkNotNull(entity, "entity == null!");
  }

  @Override
  public NBTTagCompound getTag() {
    return entity.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void setTag(NBTTagCompound tag) {
    entity.readFromNBT(tag);
  }
}
