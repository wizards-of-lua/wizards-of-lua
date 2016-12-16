package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.NBTTagUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.impl.ImmutableTable;

public class EntityWrapper extends StructuredLuaWrapper<Entity> {
  public EntityWrapper(@Nullable Entity delegate) {
    super(delegate);
  }

  // @Override
  // protected void toLuaObject(ImmutableTable.Builder builder) {
  // super.toLuaObject(builder);
  // builder.add("id", delegate.getCachedUniqueIdString());
  // builder.add("name", delegate.getName());
  // builder.add("dimension", delegate.dimension);
  // builder.add("pos", new Vec3dWrapper(delegate.getPositionVector()).getLuaObject());
  // builder.add("blockPos", new BlockPosWrapper(delegate.getPosition()).getLuaObject());
  // builder.add("orientation", new EnumWrapper(delegate.getHorizontalFacing()).getLuaObject());
  // Team team = delegate.getTeam();
  // builder.add("team", team != null ? team.getRegisteredName() : null);
  // builder.add("tags", new StringIterableWrapper(delegate.getTags()).getLuaObject());
  // }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    NBTTagCompound tagCompound = delegate.writeToNBT(new NBTTagCompound());
    NBTTagUtil.insertValues(builder, tagCompound);
  }


}
