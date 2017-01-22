package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.karneim.luamod.lua.wrapper.BlockPosWrapper;
import net.karneim.luamod.lua.wrapper.EnumWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(@Nullable E delegate, EventType eventType) {
    super(delegate, eventType);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("hand", new EnumWrapper(delegate.getHand()).getLuaObject());
    builder.add("item", new ItemStackWrapper(delegate.getItemStack()).getLuaObject());
    builder.add("pos", new BlockPosWrapper(delegate.getPos()).getLuaObject());
    builder.add("face", new EnumWrapper(delegate.getFace()).getLuaObject());
  }

}
