package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;

@LuaModule("ServerChatEvent")
public class ServerChatEventClass extends DelegatingLuaClass<ServerChatEvent> {
  public ServerChatEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends ServerChatEvent> b,
      ServerChatEvent delegate) {
    b.addReadOnly("message", () -> repo.wrap(delegate.getMessage()));
    b.addReadOnly("username", () -> repo.wrap(delegate.getUsername()));
    b.addReadOnly("player", () -> repo.wrap(delegate.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
