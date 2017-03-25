package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.event.PlayerInteractEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class PlayerInteractEventClass {

  private final String classname = "PlayerInteractEvent";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final PlayerInteractEventClass SINGLETON = new PlayerInteractEventClass();

  public static PlayerInteractEventClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public PlayerInteractEventWrapper newInstance(Table env, PlayerInteractEvent delegate,
      EventType eventType) {
    return new PlayerInteractEventWrapper(env, delegate, eventType, Metatables.get(env, classname));
  }

}
