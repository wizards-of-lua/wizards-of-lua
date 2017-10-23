package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = ChatEventClass.METATABLE_NAME, superclassname = EventClass.METATABLE_NAME)
public class ChatEventClass
    extends ProxyingLuaClass<ServerChatEvent, ChatEventClass.Proxy<ServerChatEvent>> {
  public static final String METATABLE_NAME = "ChatEvent";

  @Override
  protected String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<ServerChatEvent> toLua(ServerChatEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends ServerChatEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("message", getConverters().toLua(delegate.getMessage()));
    }
  }
}
