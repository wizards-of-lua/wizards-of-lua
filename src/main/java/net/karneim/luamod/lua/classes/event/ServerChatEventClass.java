package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.event.ServerChatEventInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.event.ServerChatEvent;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class ServerChatEventClass {

  private final String classname = "ServerChatEvent";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final ServerChatEventClass SINGLETON = new ServerChatEventClass();

  public static ServerChatEventClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public ServerChatEventInstance newInstance(Table env, ServerChatEvent delegate) {
    return new ServerChatEventInstance(env, delegate, Metatables.get(env, classname));
  }

}
