package net.karneim.luamod.lua.wrapper;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class EntityPlayerWrapper extends LuaWrapper<EntityPlayer> {
  public EntityPlayerWrapper(@Nullable EntityPlayer delegate) {
    super(delegate);
  }

  @Override
  protected Table toLuaObject() {
    Table result = DefaultTable.factory().newTable();
    // delegate.getAbsorptionAmount();
    result.rawset("armor", new ArmorWrapper(delegate.getArmorInventoryList()).getLuaObject());
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    result.rawset("health", delegate.getHealth());
    result.rawset("mainHand", new ItemStackWrapper(delegate.getHeldItemMainhand()).getLuaObject());
    result.rawset("offHand", new ItemStackWrapper(delegate.getHeldItemOffhand()).getLuaObject());
    // delegate.getInventoryEnderChest()
    result.rawset("name", delegate.getName());
    Team team = delegate.getTeam();
    result.rawset("team", team != null ? team.getRegisteredName() : null);
    result.rawset("orientation", new EnumWrapper(delegate.getHorizontalFacing()).getLuaObject());
    result.rawset("pos", new Vec3dWrapper(delegate.getPositionVector()).getLuaObject());
    result.rawset("blockPos", new BlockPosWrapper(delegate.getPosition()).getLuaObject());
    result.rawset("dimension", delegate.dimension);
    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      GameType e = mp.interactionManager.getGameType();
      result.rawset("gamemode", new EnumWrapper(e).getLuaObject());
    }

    return result;
  }

  private class ArmorWrapper extends LuaWrapper<Iterable<ItemStack>> {
    public ArmorWrapper(@Nullable Iterable<ItemStack> delegate) {
      super(delegate);
    }

    @Override
    protected Table toLuaObject() {
      Table result = DefaultTable.factory().newTable();
      Iterator<ItemStack> it = delegate.iterator();
      result.rawset("feet", new ItemStackWrapper(it.next()).getLuaObject());
      result.rawset("legs", new ItemStackWrapper(it.next()).getLuaObject());
      result.rawset("chest", new ItemStackWrapper(it.next()).getLuaObject());
      result.rawset("head", new ItemStackWrapper(it.next()).getLuaObject());
      return result;
    }
  }
}
