package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.event.Player2EventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class Player2EventClass {

  private final String classname = "Player2Event";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final Player2EventClass SINGLETON = new Player2EventClass();

  public static Player2EventClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public Player2EventWrapper newInstance(Table env, PlayerEvent delegate, EventType eventType) {
    return new Player2EventWrapper(env, delegate, eventType, Metatables.get(env, classname));
  }

}
