package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.event.PlayerInteractEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@LuaClass("PlayerInteractEvent")
public class PlayerInteractEventClass extends AbstractLuaType {
  public PlayerInteractEventWrapper newInstance(PlayerInteractEvent delegate, EventType eventType) {
    return new PlayerInteractEventWrapper(getRepo(), delegate, eventType,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
