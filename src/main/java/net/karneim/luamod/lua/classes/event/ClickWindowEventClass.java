package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.event.ClickWindowEventWrapper;
import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.wrapper.Metatables;

@LuaModule("ClickWindowEvent")
public class ClickWindowEventClass extends AbstractLuaType {
  public ClickWindowEventWrapper newInstance(ClickWindowEvent delegate, EventType eventType) {
    return new ClickWindowEventWrapper(getRepo(), delegate, eventType,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
