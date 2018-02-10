package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.ServerChatEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = ChatEventClass.NAME, superClass = EventClass.class)
public class ChatEventClass
    extends ProxyingLuaClass<ServerChatEvent, ChatEventClass.Proxy<ServerChatEvent>> {
  public static final String NAME = "ChatEvent";

  @Override
  public Proxy<ServerChatEvent> toLua(ServerChatEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends ServerChatEvent> extends EventClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("message", getConverters().toLua(delegate.getMessage()));
    }
  }
}
