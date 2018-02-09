package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = ChatEventClass.NAME, superClass = EventClass.class)
public class ChatEventClass
    extends ProxyingLuaClass<ServerChatEvent, ChatEventClass.Proxy<ServerChatEvent>> {
  public static final String NAME = "ChatEvent";

  @Override
  public Proxy<ServerChatEvent> toLua(ServerChatEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends ServerChatEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("message", getConverters().toLua(delegate.getMessage()));
    }
  }
}
