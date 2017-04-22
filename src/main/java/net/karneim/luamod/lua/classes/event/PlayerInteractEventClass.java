package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;
import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.event.PlayerInteractEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@TypeName("PlayerInteractEvent")
@ModulePackage(Constants.MODULE_PACKAGE)
public class PlayerInteractEventClass extends AbstractLuaType {
  public PlayerInteractEventWrapper newInstance(PlayerInteractEvent delegate, EventType eventType) {
    return new PlayerInteractEventWrapper(getRepo(), delegate, eventType,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
