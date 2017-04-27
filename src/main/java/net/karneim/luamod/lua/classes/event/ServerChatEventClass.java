package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;

@LuaModule("ServerChatEvent")
public class ServerChatEventClass extends DelegatingLuaClass<ServerChatEvent> {
  public ServerChatEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, ServerChatEvent event) {
    b.add("message", repo.wrap(event.getMessage()));
    b.add("username", repo.wrap(event.getUsername()));
    b.add("player", repo.wrap(event.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
