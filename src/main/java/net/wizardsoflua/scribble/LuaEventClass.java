package net.wizardsoflua.scribble;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;

@DeclareLuaClass(name = "Event", superClass = ObjectClass.class)
public class LuaEventClass
    extends ProxyCachingLuaClass<Event, LuaEventProxy<LuaEvent<Event>, Event>> {
  @Override
  protected LuaEventProxy<LuaEvent<Event>, Event> toLua(Event javaObject) {
    return new LuaEventProxy<>(new LuaEvent<>(this, javaObject));
  }
}
