package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@HasLuaClass(luaClass = LivingEventClass.class, luaInstance = LivingEventClass.Proxy.class)
public class LivingEventApi<D extends LivingEvent> extends EventApi<D> {
  public LivingEventApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
