package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;
import net.karneim.luamod.lua.event.ServerChatEventInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.event.ServerChatEvent;

@TypeName("ServerChatEvent")
@ModulePackage(Constants.MODULE_PACKAGE)
public class ServerChatEventClass extends AbstractLuaType {
  public ServerChatEventInstance newInstance(ServerChatEvent delegate) {
    return new ServerChatEventInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
