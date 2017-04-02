package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.EntityPlayerClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;

// TODO merge this class with PlayerEventWrapper
public class Player2EventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public Player2EventWrapper(LuaTypesRepo repo, @Nullable E event, EventType eventType,
      Table metatable) {
    super(repo, event, eventType.name(), metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("player",
        getRepo().get(EntityPlayerClass.class).newInstance(delegate.player).getLuaObject());
  }
}
