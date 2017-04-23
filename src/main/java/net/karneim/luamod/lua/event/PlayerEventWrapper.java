package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.EntityPlayerClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

public class PlayerEventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public PlayerEventWrapper(LuaTypesRepo repo, @Nullable E event, String eventType,
      Table metatable) {
    super(repo, event, eventType, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("player",
        repo.get(EntityPlayerClass.class).newInstance(delegate.getEntityPlayer()).getLuaObject());
  }
}
