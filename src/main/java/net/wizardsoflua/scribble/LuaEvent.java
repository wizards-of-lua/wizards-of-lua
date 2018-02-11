package net.wizardsoflua.scribble;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@LuaModule(name = "Event", superClass = ObjectClass.class)
public class LuaEvent extends LuaApiBase<Event> {
  public LuaEvent(ProxyingLuaClass<?, ?> luaClass, Event delegate) {
    super(luaClass, delegate);
  }

  @LuaProperty
  public String getName() {
    return getLuaClass().getName();
  }
}
