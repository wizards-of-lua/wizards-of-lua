package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.EnumClass;
import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

public class PlayerInteractEventWrapper<E extends PlayerInteractEvent>
    extends PlayerEventWrapper<E> {
  public PlayerInteractEventWrapper(LuaTypesRepo repo, @Nullable E delegate, EventType eventType,
      Table metatable) {
    super(repo, delegate, eventType, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("hand",
        EnumClass.get().newInstance(repo, delegate.getHand()).getLuaObject());
    builder.addNullable("item",
        repo.get(ItemStackClass.class).newInstance(delegate.getItemStack()).getLuaObject());
    builder.addNullable("pos",
        repo.get(Vec3Class.class).newInstance(delegate.getPos()).getLuaObject());
    builder.addNullable("face",
        EnumClass.get().newInstance(repo, delegate.getFace()).getLuaObject());
  }

}
