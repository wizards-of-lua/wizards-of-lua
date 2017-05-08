package net.karneim.luamod.lua.wrapper.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NbtCompoundChildAccessor implements NbtAccessor<NBTBase> {
  private final NbtAccessor<NBTTagCompound> parent;
  private final String key;

  public NbtCompoundChildAccessor(NbtAccessor<NBTTagCompound> parent, String key) {
    this.parent = checkNotNull(parent, "parent == null!");
    this.key = checkNotNull(key, "key == null!");
  }

  @Override
  public NBTBase getTag() {
    NBTTagCompound parentTag = parent.getTag();
    NBTBase result = parentTag.getTag(key);
    return result;
  }

  @Override
  public void setTag(NBTBase tag) {
    // refresh the parent tag before updating
    NBTTagCompound parentTag = parent.getTag();
    parentTag.setTag(key, tag);
    parent.setTag(parentTag);
  }
}
