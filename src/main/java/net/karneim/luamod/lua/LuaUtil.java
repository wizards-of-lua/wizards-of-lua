package net.karneim.luamod.lua;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

import net.karneim.luamod.Entities;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.Players;
import net.karneim.luamod.cache.LuaFunctionBinaryCache;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Snapshots;
import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.event.Events;
import net.karneim.luamod.lua.patched.ExtendedChunkLoader;
import net.karneim.luamod.lua.patched.PatchedCompilerChunkLoader;
import net.karneim.luamod.lua.wrapper.ClipboardWrapper;
import net.karneim.luamod.lua.wrapper.EntitiesWrapper;
import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.karneim.luamod.lua.wrapper.EntityWrapper;
import net.karneim.luamod.lua.wrapper.EventsWrapper;
import net.karneim.luamod.lua.wrapper.PlayersWrapper;
import net.karneim.luamod.lua.wrapper.RuntimeWrapper;
import net.karneim.luamod.lua.wrapper.SpellWrapper;
import net.karneim.luamod.lua.wrapper.Vec3dWrapper;
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

public class LuaUtil {
  private final World world;
  private final Credentials credentials;
  private final StateContext state;
  private final Table env;
  private final ChunkLoader loader;
  private final DirectCallExecutor executor;
  private final Clipboard clipboard;
  private LuaFunction headerFunc;
  private LuaFunction dummyFunc;
  private LuaFunction commandLineFunc;

  private Ticks ticks;
  private Events events;
  private Runtime runtime;
  private List<Requirement> standardRequirements = new ArrayList<>();

  public LuaUtil(World world, ICommandSender owner, Spell spell, Clipboard clipboard,
      Credentials credentials) {
    this.world = world;
    this.clipboard = clipboard;
    this.credentials = credentials;
    state = StateContexts.newDefaultInstance();
    env = state.newTable();
    loader = CompilerChunkLoader.of("LuaProgramAsJavaByteCode");

    ticks = new Ticks(LuaMod.instance.getDefaultTicksLimit());
    events = new Events(env, LuaMod.instance.getSpellRegistry());
    runtime = new Runtime(ticks);

    ExtendedChunkLoader modulesLoader = PatchedCompilerChunkLoader.of("RequiredModulesAsByteCode");
    RuntimeEnvironment environment = getModRuntimeEnvironment();

    BasicLib.installInto(state, env, environment, modulesLoader);
    ModuleLib.installInto(state, env, environment, /* modulesLoader */ null,
        ClassLoader.getSystemClassLoader());

    CoroutineLib.installInto(state, env);
    StringLib.installInto(state, env);
    MathLib.installInto(state, env);
    TableLib.installInto(state, env);
    // IoLib.installInto(state, env, environment);
    OsLib.installInto(state, env, environment);
    Utf8Lib.installInto(state, env);

    LuaFunctionBinaryCache luaFunctionCache = LuaMod.instance.getLuaFunctionCache();
    ClasspathResourceSearcher.installInto(env, modulesLoader, luaFunctionCache, LuaUtil.class.getClassLoader());

    GistRepo gistRepo = LuaMod.instance.getGistRepo();
    GistSearcher.installInto(env, modulesLoader, luaFunctionCache, gistRepo, credentials);
    
    LuaModLib.installInto(env, owner);
    
    Snapshots snapshots = new Snapshots();

    SpellWrapper.installInto(env, spell, events, snapshots);
    ClipboardWrapper.installInto(env, clipboard, snapshots);
    EventsWrapper.installInto(env, events);
    RuntimeWrapper.installInto(env, runtime);
    PlayersWrapper.installInto(env, new Players(LuaMod.instance.getServer(), owner));
    EntitiesWrapper.installInto(env, new Entities(LuaMod.instance.getServer(), owner));

    require("inspect", "net.karneim.luamod.lua.classes.inspect");
    require("net.karneim.luamod.lua.classes.Globals");
    require("net.karneim.luamod.lua.classes.string");
    require(Vec3dWrapper.MODULE);
    require(EntityWrapper.MODULE);
    require(EntityPlayerWrapper.MODULE);
    
    SchedulingContextFactory schedulingContextFactory = new SchedulingContextFactory() {

      @Override
      public SchedulingContext newInstance() {
        return new SchedulingContextImpl(events, ticks, runtime);
      }
    };
    executor = DirectCallExecutor.newExecutor(schedulingContextFactory);

  }

  private void require(String name, String module) {
    standardRequirements.add(new Requirement(name, module));
  }

  public void require(String module) {
    standardRequirements.add(new Requirement(module));
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

  public void compile(String commandLine) throws LoaderException {
    // Add Vec3 requirement
    StringBuilder buf = new StringBuilder();
    for (Requirement requirement : standardRequirements) {
      if ( requirement.name != null) {
        buf.append(requirement.name).append(" = ");
      }
      buf.append("require ").append("\"").append(requirement.module).append("\"").append("\n");
    }
    String header = buf.toString();
    headerFunc = loader.loadTextChunk(new Variable(env), "header", header);
    dummyFunc = loader.loadTextChunk(new Variable(env), "header", "dummy=1");
    commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", commandLine);
    System.out.println(String.format("commandLine='%s'",commandLine));
  }

  public void run() throws CallException, CallPausedException, InterruptedException {
    executor.call(state, headerFunc);
    executor.call(state, dummyFunc);
    EntityPlayerWrapper.addFunctions(env);
    EntityWrapper.addFunctions(env);
    executor.call(state, commandLineFunc);
  }

  public void resume(Continuation continuation)
      throws CallException, CallPausedException, InterruptedException {
    executor.resume(continuation);
  }

  public boolean isWaiting() {
    return runtime.isSleeping() || events.isWaitingForEvent();
  }

  public void setCurrentTime(int ticksExisted) {
    events.setCurrentTime(ticksExisted);
    runtime.setCurrentTime(ticksExisted);
  }
  
  class Requirement {
    String name;
    String module;
    
    public Requirement(String name, String module) {
      this.name = name;
      this.module = module;
    }
    
    public Requirement(String module) {
      this.name = null;
      this.module = module;
    }
    
  }

}
