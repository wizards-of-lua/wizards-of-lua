package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
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
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("hand", new EnumWrapper(delegate.getHand()).getLuaObject());
    builder.addNullable("item", new ItemStackWrapper(delegate.getItemStack()).getLuaObject());
    builder.addNullable("pos", new BlockPosWrapper(delegate.getPos()).getLuaObject());
    builder.addNullable("face", new EnumWrapper(delegate.getFace()).getLuaObject());
  }

}
