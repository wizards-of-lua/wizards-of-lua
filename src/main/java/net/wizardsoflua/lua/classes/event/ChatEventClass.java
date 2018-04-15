package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.ServerChatEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = ChatEventClass.NAME, superClass = EventClass.class)
public class ChatEventClass
    extends DelegatorLuaClass<ServerChatEvent, ChatEventClass.Proxy<ServerChatEvent>> {
  public static final String NAME = "ChatEvent";

  @Override
  public Proxy<ServerChatEvent> toLua(ServerChatEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends ServerChatEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("message", getConverters().toLua(delegate.getMessage()));
    }
  }
}
