package net.karneim.luamod.lua;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import net.karneim.luamod.Entities;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.Players;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Cursor;
import net.karneim.luamod.cursor.Snapshots;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.event.Events;
import net.karneim.luamod.lua.wrapper.ClipboardWrapper;
import net.karneim.luamod.lua.wrapper.CursorWrapper;
import net.karneim.luamod.lua.wrapper.EntitiesWrapper;
import net.karneim.luamod.lua.wrapper.EventsWrapper;
import net.karneim.luamod.lua.wrapper.PlayersWrapper;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.compiler.CompilerChunkLoader;
import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.CoroutineLib;
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.OsLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.lib.Utf8Lib;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.sandius.rembulan.util.Check;

public class LuaUtil {
  private final World world;
  private final Credentials credentials;
  private final StateContext state;
  private final Table env;
  private final ChunkLoader loader;
  private final DirectCallExecutor executor;
  private final Clipboard clipboard;
  private LuaFunction main;

  private Events events;

  public LuaUtil(World world, ICommandSender owner, Cursor cursor, Clipboard clipboard,
      Credentials credentials) {
    this.world = world;
    this.clipboard = clipboard;
    this.credentials = credentials;
    state = StateContexts.newDefaultInstance();
    env = state.newTable();
    loader = CompilerChunkLoader.of("LuaProgramAsJavaByteCode");

    events = new Events(LuaMod.instance);

    ChunkLoader modulesLoader = CompilerChunkLoader.of("RequiredModulesAsByteCode");
    RuntimeEnvironment environment = getModRuntimeEnvironment();

    BasicLib.installInto(state, env, environment, modulesLoader);
    ModuleLib.installInto(state, env, environment, modulesLoader,
        ClassLoader.getSystemClassLoader());

    CoroutineLib.installInto(state, env);
    StringLib.installInto(state, env);
    MathLib.installInto(state, env);
    TableLib.installInto(state, env);
    // IoLib.installInto(state, env, environment);
    OsLib.installInto(state, env, environment);
    Utf8Lib.installInto(state, env);

    GistRepo gistRepo = LuaMod.instance.getGistRepo();
    GistSearcher.installInto(env, modulesLoader, gistRepo, credentials);

    LuaModLib.installInto(env, owner);
    Snapshots snapshots = new Snapshots();

    CursorWrapper.installInto(env, cursor, events, snapshots);
    ClipboardWrapper.installInto(env, clipboard, snapshots);
    EventsWrapper.installInto(env, events);
    PlayersWrapper.installInto(env, new Players(LuaMod.instance.getServer(), owner));
    EntitiesWrapper.installInto(env, new Entities(LuaMod.instance.getServer(), owner));

    class SleepableCountDownSchedulingContext implements SchedulingContext {

      private long allowance;

      public SleepableCountDownSchedulingContext(long max) {
        Check.nonNegative(max);
        this.allowance = max;
      }

      @Override
      public void registerTicks(int ticks) {
        allowance -= Math.max(0, ticks);
      }

      @Override
      public boolean shouldPause() {
        if (allowance <= 0) {
          return true;
        }
        if (events.isWaiting()) {
          return true;
        }
        return false;
      }
    }
    SchedulingContextFactory schedulingContextFactory = new SchedulingContextFactory() {

      @Override
      public SchedulingContext newInstance() {
        return new SleepableCountDownSchedulingContext(LuaMod.instance.getDefaultTicksLimit());
      }
    };
    executor = DirectCallExecutor.newExecutor(schedulingContextFactory);

  }

  private RuntimeEnvironment getModRuntimeEnvironment() {
    return new RuntimeEnvironment() {
      RuntimeEnvironment delegate = RuntimeEnvironments.system();

      @Override
      public InputStream standardInput() {
        return delegate.standardInput();
      }

      @Override
      public OutputStream standardOutput() {
        return delegate.standardOutput();
      }

      @Override
      public OutputStream standardError() {
        return delegate.standardError();
      }

      @Override
      public FileSystem fileSystem() {
        return delegate.fileSystem();
      }

      @Override
      public String getEnv(String name) {
        // Make sure that lua modules are loaded from this mod's config dir only
        // TODO ensure that the "package.path" variable can not be changed by users during runtime
        if ("LUA_PATH".equals(name)) {
          return LuaMod.instance.getLuaDir().getAbsolutePath() + "/?.lua";
        }
        return delegate.getEnv(name);
      }

      @Override
      public double getCpuTime() {
        return delegate.getCpuTime();
      }
    };
  }

  public Events getEvents() {
    return events;
  }

  public void compile(String program) throws LoaderException {
    main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);
  }

  public void run() throws CallException, CallPausedException, InterruptedException {
    executor.call(state, main);
  }

  public void resume(Continuation continuation)
      throws CallException, CallPausedException, InterruptedException {
    executor.resume(continuation);
  }


}
