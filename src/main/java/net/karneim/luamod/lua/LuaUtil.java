package net.karneim.luamod.lua;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.karneim.luamod.Blocks;
import net.karneim.luamod.Entities;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.Players;
import net.karneim.luamod.cache.LuaFunctionBinaryCache;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Snapshots;
import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.classes.ArmorClass;
import net.karneim.luamod.lua.classes.BlockStateClass;
import net.karneim.luamod.lua.classes.EntityClass;
import net.karneim.luamod.lua.classes.EntityLivingClass;
import net.karneim.luamod.lua.classes.EntityPlayerClass;
import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.MaterialClass;
import net.karneim.luamod.lua.classes.SpellClass;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.classes.event.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.GenericLuaEventClass;
import net.karneim.luamod.lua.classes.event.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.WhisperEventClass;
import net.karneim.luamod.lua.event.Events;
import net.karneim.luamod.lua.patched.ExtendedChunkLoader;
import net.karneim.luamod.lua.patched.PatchedCompilerChunkLoader;
import net.karneim.luamod.lua.wrapper.BlocksWrapper;
import net.karneim.luamod.lua.wrapper.ClipboardWrapper;
import net.karneim.luamod.lua.wrapper.EntitiesWrapper;
import net.karneim.luamod.lua.wrapper.EventsWrapper;
import net.karneim.luamod.lua.wrapper.PlayersWrapper;
import net.karneim.luamod.lua.wrapper.RuntimeWrapper;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
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
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.parser.ParseException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;

public class LuaUtil {
  private final World world;
  private final Credentials credentials;
  private final StateContext state;
  private final Table env;
  private final ExtendedChunkLoader loader;
  private final DirectCallExecutor executor;
  private final Clipboard clipboard;
  private final LuaTypesRepo typesRepo;
  private LuaFunction headerFunc;
  private List<LuaFunction> profileFuncs = new ArrayList<>();
  private LuaFunction commandLineFunc;

  private LuaTicks ticks;
  private Events events;
  private Runtime runtime;
  private List<Requirement> standardRequirements = new ArrayList<>();
  private List<String> profiles;
  private Spell spell;
  private String commandLine;
  private Entities entities;
  private SpellEntity entity;
  private Blocks blocks;

  public LuaUtil(World world, SpellEntity entity, ICommandSender owner, Spell spell,
      Clipboard clipboard, Credentials credentials, Snapshots snapshots) {
    this.world = world;
    this.entity = entity;
    this.spell = spell;
    this.clipboard = clipboard;
    this.credentials = credentials;

    state = StateContexts.newDefaultInstance();
    env = state.newTable();

    entities = new Entities(LuaMod.instance.getServer(), entity);
    typesRepo = new LuaTypesRepo(env);
    typesRepo.register(new Vec3Class());
    typesRepo.register(new MaterialClass());
    typesRepo.register(new ItemStackClass());
    typesRepo.register(new BlockStateClass());
    typesRepo.register(new ArmorClass());
    typesRepo.register(new EntityClass(entities));
    typesRepo.register(new EntityLivingClass());
    typesRepo.register(new EntityPlayerClass());
    typesRepo.register(new SpellClass());
    typesRepo.register(new EventClass());
    typesRepo.register(new GenericLuaEventClass());
    typesRepo.register(new AnimationHandEventClass());
    typesRepo.register(new PlayerEventClass());
    typesRepo.register(new PlayerInteractEventClass());
    typesRepo.register(new ServerChatEventClass());
    typesRepo.register(new WhisperEventClass());

    blocks = new Blocks(world);

    loader = PatchedCompilerChunkLoader.of("LuaProgramAsJavaByteCode");
    ticks = new LuaTicks(LuaMod.instance.getTicksLimit());
    events = new Events(typesRepo, LuaMod.instance.getSpellRegistry());
    runtime = new Runtime(world, ticks);

    RuntimeEnvironment environment = getModRuntimeEnvironment();

    BasicLib.installInto(state, env, environment, loader);
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
    ClasspathResourceSearcher.installInto(env, loader, luaFunctionCache,
        LuaUtil.class.getClassLoader());

    GistRepo gistRepo = LuaMod.instance.getGistRepo();
    GistSearcher.installInto(env, loader, luaFunctionCache, gistRepo, credentials);

    LuaModLib.installInto(env, owner);

    // SpellWrapper.installInto(env, spell, events, snapshots);
    ClipboardWrapper.installInto(env, clipboard, snapshots);
    EventsWrapper.installInto(env, events);
    RuntimeWrapper.installInto(env, runtime);
    PlayersWrapper.installInto(typesRepo, new Players(LuaMod.instance.getServer(), owner));
    EntitiesWrapper.installInto(typesRepo, entities);
    BlocksWrapper.installInto(typesRepo, blocks);

    require("inspect", "net.karneim.luamod.lua.classes.inspect");
    require("net.karneim.luamod.lua.classes.Globals");
    require("net.karneim.luamod.lua.classes.string");

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

  private void require(String module) {
    standardRequirements.add(new Requirement(module));
  }

  public void setProfiles(List<String> profiles) {
    this.profiles = profiles;
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

  public void setCommand(String command) {
    this.commandLine = command;
  }

  private void compile() throws LoaderException {
    StringBuilder buf = new StringBuilder();
    for (Requirement requirement : standardRequirements) {
      if (requirement.name != null) {
        buf.append(requirement.name).append(" = ");
      }
      buf.append("require ").append("\"").append(requirement.module).append("\"").append("\n");
    }
    String header = buf.toString();
    headerFunc = loader.loadTextChunk(new Variable(env), "header", header);
    if (profiles != null) {
      for (String profile : profiles) {
        profileFuncs.add(loader.loadTextChunk(new Variable(env), profile,
            String.format("require \"%s\"\n", profile)));
      }
    }
    commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", commandLine);
  }

  public void run() throws Exception {
    try {
      this.compile();
      executor.call(state, headerFunc);

      typesRepo.get(Vec3Class.class).installInto(loader, executor, state);
      typesRepo.get(MaterialClass.class).installInto(loader, executor, state);
      typesRepo.get(ItemStackClass.class).installInto(loader, executor, state);
      typesRepo.get(BlockStateClass.class).installInto(loader, executor, state);
      typesRepo.get(ArmorClass.class).installInto(loader, executor, state);
      typesRepo.get(EntityClass.class).installInto(loader, executor, state);
      typesRepo.get(EntityLivingClass.class).installInto(loader, executor, state);
      typesRepo.get(EntityPlayerClass.class).installInto(loader, executor, state);
      typesRepo.get(SpellClass.class).installInto(loader, executor, state);
      
      typesRepo.get(EventClass.class).installInto(loader, executor, state);
      typesRepo.get(AnimationHandEventClass.class).installInto(loader, executor, state);
      typesRepo.get(GenericLuaEventClass.class).installInto(loader, executor, state);
      typesRepo.get(PlayerEventClass.class).installInto(loader, executor, state);
      typesRepo.get(PlayerInteractEventClass.class).installInto(loader, executor, state);
      typesRepo.get(ServerChatEventClass.class).installInto(loader, executor, state);
      typesRepo.get(WhisperEventClass.class).installInto(loader, executor, state);
      
      env.rawset("spell", typesRepo.get(SpellClass.class).newInstance(this.spell).getLuaObject());

      for (LuaFunction profileFunc : profileFuncs) {
        executor.call(state, profileFunc);
      }
      executor.call(state, commandLineFunc);
    } catch (CallException ex) {
      throw newLuaException(ex);
    } catch (LuaRuntimeException ex) {
      throw newLuaException(ex);
    } catch (Exception e) {
      if (e.getCause() instanceof ParseException) {
        throw newLuaException((ParseException) e.getCause());
      }
      throw e;
    }
  }

  private LuaException newLuaException(Exception cause) {
    String exMessage = getExceptionMessage(cause);
    if (exMessage == null) {
      exMessage = "Unknown error";
    }
    String strace = toString(cause);
    Pattern p = Pattern.compile("LuaProgramAsJavaByteCode.*\\.run\\((.+):(.*)\\)");
    Matcher m = p.matcher(strace);
    StringBuilder modules = new StringBuilder();
    while (m.find()) {
      String module = m.group(1);
      String line = m.group(2);
      if (modules.length() > 0) {
        modules.append("\n");
      }
      modules.append(" at line ").append(line).append(" of ").append(module);
    }
    if (modules.length() > 0) {
      String message = String.format("%s\n%s!", exMessage, modules.toString());
      return new LuaException(message, cause);
    }
    return new LuaException(exMessage, cause);
  }

  private String getExceptionMessage(Throwable top) {
    Throwable cause = top;
    while (cause != null && !(cause instanceof ParseException)) {
      cause = cause.getCause();
    }
    if (cause instanceof ParseException) {
      return cause.getMessage();
    }
    cause = top;
    while (cause != null && !(cause instanceof LuaRuntimeException)) {
      cause = cause.getCause();
    }
    if (cause instanceof LuaRuntimeException) {
      return cause.getMessage();
    }
    return top.getMessage();
  }

  private String toString(Exception cause) {
    StringWriter writer = new StringWriter();
    cause.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public void resume(Continuation continuation)
      throws Exception {
    try {
      executor.resume(continuation);
    } catch (CallException ex) {
      throw newLuaException(ex);
    } catch (LuaRuntimeException ex) {
      throw newLuaException(ex);
    } catch (Exception e) {
      if (e.getCause() instanceof ParseException) {
        throw newLuaException((ParseException) e.getCause());
      }
      throw e;
    }
  }

  public boolean isWaiting() {
    return runtime.isSleeping() || events.isWaitingForEvent();
  }

  public void setCurrentTime(int ticksExisted) {
    events.setCurrentTime(ticksExisted);
    runtime.setSpellLifetime(ticksExisted);
  }

  public Runtime getRuntime() {
    return runtime;
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
