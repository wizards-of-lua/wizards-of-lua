package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.BlockPosWrapper;
import net.karneim.luamod.lua.wrapper.EnumWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(Table env, @Nullable E delegate, EventType eventType) {
    super(env, delegate, eventType);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("hand", new EnumWrapper(env, delegate.getHand()).getLuaObject());
    builder.addNullable("item", new ItemStackWrapper(env, delegate.getItemStack()).getLuaObject());
    builder.addNullable("pos", new BlockPosWrapper(env, delegate.getPos()).getLuaObject());
    builder.addNullable("face", new EnumWrapper(env, delegate.getFace()).getLuaObject());
  }

}
