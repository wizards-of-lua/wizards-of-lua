package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;
import net.karneim.luamod.lua.event.GenericLuaEventInstance;
import net.karneim.luamod.lua.wrapper.Metatables;

@TypeName("GenericEvent")
@ModulePackage(Constants.MODULE_PACKAGE)
public class GenericLuaEventClass extends AbstractLuaType {
  public GenericLuaEventInstance newInstance(Object delegate, String name) {
    return new GenericLuaEventInstance(getRepo(), delegate, name,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
