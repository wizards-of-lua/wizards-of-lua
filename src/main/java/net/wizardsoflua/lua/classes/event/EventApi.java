package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.lua.classes.LuaClassApi;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@HasLuaClass(luaClass = EventClass.class, luaInstance = EventClass.Proxy.class)
public class EventApi<D extends Event> extends LuaClassApi<D> {
  public EventApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
