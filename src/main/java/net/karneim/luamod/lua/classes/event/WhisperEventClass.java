package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.event.WhisperEventWrapper;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class WhisperEventClass {

  private final String classname = "WhisperEvent";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final WhisperEventClass SINGLETON = new WhisperEventClass();

  public static WhisperEventClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public WhisperEventWrapper newInstance(Table env, WhisperEvent delegate) {
    return new WhisperEventWrapper(env, delegate, Metatables.get(env, classname));
  }

}
