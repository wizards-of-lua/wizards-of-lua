package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.event.WhisperEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;

@TypeName("WhisperEvent")
@ModulePackage(Constants.MODULE_PACKAGE)
public class WhisperEventClass extends AbstractLuaType {
  public WhisperEventWrapper newInstance(WhisperEvent delegate) {
    return new WhisperEventWrapper(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
