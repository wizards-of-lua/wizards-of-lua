package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.BlockPosWrapper;
import net.karneim.luamod.lua.wrapper.EnumWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.impl.ImmutableTable;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(@Nullable E delegate, EventType eventType) {
    super(delegate, eventType);
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    builder.add("hand", new EnumWrapper(delegate.getHand()).getLuaObject());
    builder.add("item", new ItemStackWrapper(delegate.getItemStack()).getLuaObject());
    builder.add("pos", new BlockPosWrapper(delegate.getPos()).getLuaObject());
    builder.add("face", new EnumWrapper(delegate.getFace()).getLuaObject());
  }


}
