package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.lua.classes.LuaClassApi;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@HasLuaClass(luaClass = EventClass.class, luaInstance = EventClass.Proxy.class)
public class EventApi<D extends Event> extends LuaClassApi<D> {
  public EventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
