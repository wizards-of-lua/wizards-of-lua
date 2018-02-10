package net.wizardsoflua.scribble;

import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.event.EventClass;

@DeclareLuaClass(name = "SwingArmEvent", superClass = EventClass.class)
public class LuaSwingArmEventClass
    extends ProxyCachingLuaClass<SwingArmEvent, LuaSwingArmEventProxy> {
  @Override
  protected LuaSwingArmEventProxy toLua(SwingArmEvent javaObject) {
    return new LuaSwingArmEventProxy(new LuaSwingArmEvent(this, javaObject));
  }
}
