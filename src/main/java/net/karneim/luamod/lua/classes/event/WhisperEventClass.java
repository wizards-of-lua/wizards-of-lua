package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.event.WhisperEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;

@LuaClass("WhisperEvent")
public class WhisperEventClass extends AbstractLuaType {
  public WhisperEventWrapper newInstance(WhisperEvent delegate) {
    return new WhisperEventWrapper(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
