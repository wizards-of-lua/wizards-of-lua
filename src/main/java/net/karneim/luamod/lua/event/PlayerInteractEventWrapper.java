package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.BlockPosWrapper;
import net.karneim.luamod.lua.wrapper.EnumWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackWrapper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(@Nullable E delegate, EventType eventType) {
    super(delegate, eventType);
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("hand", new EnumWrapper(delegate.getHand()).getLuaObject());
    result.rawset("item", new ItemStackWrapper(delegate.getItemStack()).getLuaObject());
    result.rawset("pos", new BlockPosWrapper(delegate.getPos()).getLuaObject());
    result.rawset("face", new EnumWrapper(delegate.getFace()).getLuaObject());
    return result;
  }


}
