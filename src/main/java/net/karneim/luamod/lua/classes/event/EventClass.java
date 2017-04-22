package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;

@TypeName("Event")
@ModulePackage(Constants.MODULE_PACKAGE)
public class EventClass extends AbstractLuaType {
  @Override
  protected void addFunctions() {}
}
