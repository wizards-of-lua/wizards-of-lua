package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.EnumClass;
import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(Table env, @Nullable E delegate, EventType eventType, Table metatable) {
    super(env, delegate, eventType, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("hand", EnumClass.get().newInstance(env, delegate.getHand()).getLuaObject());
    builder.addNullable("item", ItemStackClass.get().newInstance(env, delegate.getItemStack()).getLuaObject());
    builder.addNullable("pos", Vec3Class.get().newInstance(env, delegate.getPos()).getLuaObject());
    builder.addNullable("face", EnumClass.get().newInstance(env, delegate.getFace()).getLuaObject());
  }

}
