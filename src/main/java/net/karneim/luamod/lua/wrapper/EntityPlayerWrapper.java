package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameType;
import net.sandius.rembulan.impl.ImmutableTable;

public class EntityPlayerWrapper extends StructuredLuaWrapper<EntityPlayer> {
  public EntityPlayerWrapper(@Nullable EntityPlayer delegate) {
    super(delegate);
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()
    builder.add("armor", new ArmorWrapper(delegate.getArmorInventoryList()).getLuaObject());
    builder.add("health", delegate.getHealth());
    builder.add("mainHand", new ItemStackWrapper(delegate.getHeldItemMainhand()).getLuaObject());
    builder.add("offHand", new ItemStackWrapper(delegate.getHeldItemOffhand()).getLuaObject());
    builder.add("name", delegate.getName());
    Team team = delegate.getTeam();
    builder.add("team", team != null ? team.getRegisteredName() : null);
    builder.add("orientation", new EnumWrapper(delegate.getHorizontalFacing()).getLuaObject());
    builder.add("pos", new Vec3dWrapper(delegate.getPositionVector()).getLuaObject());
    builder.add("blockPos", new BlockPosWrapper(delegate.getPosition()).getLuaObject());
    builder.add("dimension", delegate.dimension);
    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      GameType e = mp.interactionManager.getGameType();
      builder.add("gamemode", new EnumWrapper(e).getLuaObject());
    }
  }

}
