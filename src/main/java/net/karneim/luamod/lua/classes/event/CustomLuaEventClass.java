package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.event.GenericLuaEventInstance;
import net.karneim.luamod.lua.wrapper.Metatables;

@LuaModule("CustomEvent")
public class CustomLuaEventClass extends AbstractLuaType {
  public GenericLuaEventInstance newInstance(Object delegate, String name) {
    return new GenericLuaEventInstance(getRepo(), delegate, name,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
