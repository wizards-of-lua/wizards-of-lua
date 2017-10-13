package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

@DeclareLuaClass(name = ChatEventClass.METATABLE_NAME, superclassname = EventClass.METATABLE_NAME)
public class ChatEventClass extends LuaClass<ServerChatEvent> {
  public static final String METATABLE_NAME = "ChatEvent";

  public ChatEventClass() {
    super(ServerChatEvent.class);
  }

  @Override
  public Table toLua(ServerChatEvent javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, METATABLE_NAME);
  }

  @Override
  public ServerChatEvent toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends EventClass.Proxy {

    private final ServerChatEvent delegate;

    public Proxy(Converters converters, Table metatable, ServerChatEvent delegate, String name) {
      super(converters, metatable, delegate, name);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      addImmutable("player", getConverters().toLua(delegate.getPlayer()));
      addImmutable("message", getConverters().toLua(delegate.getMessage()));
    }
  }

}
