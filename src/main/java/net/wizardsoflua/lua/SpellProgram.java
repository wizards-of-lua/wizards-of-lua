package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.lua.scheduling.LuaExecutor.Type.EVENT_LISTENER;
import static net.wizardsoflua.lua.scheduling.LuaExecutor.Type.MAIN;

import java.nio.file.FileSystem;
import java.time.Clock;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.CoroutineLib;
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.module.blocks.BlocksModule;
import net.wizardsoflua.lua.module.entities.EntitiesModule;
import net.wizardsoflua.lua.module.events.EventHandlers;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.lua.module.items.ItemsModule;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;
import net.wizardsoflua.lua.module.print.PrintRedirector;
import net.wizardsoflua.lua.module.searcher.ClasspathResourceSearcher;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.lua.module.searcher.PatchedChunkLoadPathSearcher;
import net.wizardsoflua.lua.module.spell.SpellModule;
import net.wizardsoflua.lua.module.system.SystemAdapter;
import net.wizardsoflua.lua.module.system.SystemModule;
import net.wizardsoflua.lua.module.time.Time;
import net.wizardsoflua.lua.module.time.TimeModule;
import net.wizardsoflua.lua.module.types.TypesModule;
import net.wizardsoflua.lua.scheduling.LuaExecutor;
import net.wizardsoflua.lua.scheduling.LuaSchedulingContext;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;

public class SpellProgram {
  private enum State {
    NEW, PAUSED, FINISHED;
  }
  public interface Context {
    String getLuaPathElementOfPlayer(String nameOrUuid);

    LuaFunctionBinaryCache getLuaFunctionBinaryCache();

    Clock getClock();

    int getLuaTicksLimit();

    int getEventListenerLuaTicksLimit();
  }

  private static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final String code;
  private final ModuleDependencies dependencies;
  private final LuaExecutor executor;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final RuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;
  private final LuaClassLoader luaClassLoader;
  private final EventHandlers eventHandlers;
  private ICommandSender owner;
  private State state = State.NEW;

  private Continuation continuation;
  private SpellEntity spellEntity;
  private Time time;
  private String defaultLuaPath;
  private final Context context;

  SpellProgram(ICommandSender owner, String code, ModuleDependencies dependencies,
      String defaultLuaPath, World world, SystemAdapter systemAdapter, Context context) {
    this.owner = checkNotNull(owner, "owner==null!");
    this.code = checkNotNull(code, "code==null!");
    this.dependencies = checkNotNull(dependencies, "dependencies==null!");
    this.defaultLuaPath = checkNotNull(defaultLuaPath, "defaultLuaPath==null!");
    this.context = checkNotNull(context, "context==null!");

    int luaTicksLimit = context.getLuaTicksLimit();
    int eventListenerLuaTicksLimit = context.getEventListenerLuaTicksLimit();
    executor = new LuaExecutor(luaTicksLimit, eventListenerLuaTicksLimit);
    time = new Time(world, luaTicksLimit, new Time.Context() {
      @Override
      public Clock getClock() {
        return context.getClock();
      }

      @Override
      public LuaSchedulingContext getCurrentSchedulingContext() {
        return executor.getCurrentSchedulingContext();
      }
    });
    executor.addSchedulingContext(time);

    stateContext = StateContexts.newDefaultInstance();
    env = stateContext.newTable();
    runtimeEnv = RuntimeEnvironments.system();
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory(ROOT_CLASS_PREFIX);
    installSystemLibraries();
    luaClassLoader = new LuaClassLoader(env, new LuaClassLoader.Context() {
      @Override
      public @Nullable LuaSchedulingContext getCurrentSchedulingContext() {
        return executor.getCurrentSchedulingContext();
      }
    });
    luaClassLoader.loadStandardClasses();
    TypesModule.installInto(env, luaClassLoader.getTypes(), getConverters());
    PrintRedirector.installInto(env, new PrintRedirector.Context() {
      @Override
      public void send(String message) {
        SpellProgram.this.owner.sendMessage(new TextComponentString(message));
      }
    });
    AddPathFunction.installInto(env, getConverters(), new AddPathFunction.Context() {
      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        return context.getLuaPathElementOfPlayer(nameOrUuid);
      }

      @Override
      public void addPath(String pathelement) {
        SpellProgram.this.defaultLuaPath += ";" + pathelement;
      }
    });
    TimeModule.installInto(env, luaClassLoader, time);
    SystemModule.installInto(env, luaClassLoader, systemAdapter);
    BlocksModule.installInto(env, getConverters());
    ItemsModule.installInto(env, getConverters());
    eventHandlers = new EventHandlers(luaClassLoader, createEventHandlersContext());
    executor.addSchedulingContext(new SchedulingContext() {
      @Override
      public boolean shouldPause() {
        return eventHandlers.shouldPause();
      }

      @Override
      public void registerTicks(int ticks) {
        // ignore, since not required here.
      }
    });
    executor.addSchedulingContext(new SchedulingContext() {
      @Override
      public boolean shouldPause() {
        return systemAdapter.shouldPause();
      }

      @Override
      public void registerTicks(int ticks) {
        // ignore, since not required here.
      }
    });
    EventsModule.installInto(env, luaClassLoader, eventHandlers);
  }

  public LuaClassLoader getLuaClassLoader() {
    return luaClassLoader;
  }

  private Converters getConverters() {
    return luaClassLoader.getConverters();
  }

  private EventHandlers.Context createEventHandlersContext() {
    return new EventHandlers.Context() {
      @Override
      public long getCurrentTime() {
        return time.getGameTotalTime();
      }

      @Override
      public void call(LuaFunction function, Object... args) {
        try {
          executor.call(EVENT_LISTENER, stateContext, function, args);
        } catch (CallException | CallPausedException | InterruptedException ex) {
          handleException("event handling", ex);
        }
      }
    };
  }

  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = spellEntity;
  }

  public String getCode() {
    return code;
  }

  public EventHandlers getEventHandlers() {
    return eventHandlers;
  }

  public boolean isTerminated() {
    return state == State.FINISHED && eventHandlers.getSubscriptions().isEmpty();
  }

  public void terminate() {
    state = State.FINISHED;
    eventHandlers.getSubscriptions().clear();
  }

  public void resume() {
    try {
      switch (state) {
        case NEW:
          compileAndRun();
          break;
        case PAUSED:
          executor.resume(MAIN, continuation);
          break;
        case FINISHED:
          return;
      }
      state = State.FINISHED;
    } catch (CallPausedException ex) {
      continuation = ex.getContinuation();
      state = State.PAUSED;
    } catch (Exception ex) {
      handleException("spell execution", ex);
    }
  }

  private void handleException(String during, Exception ex) {
    terminate();
    SpellException s = exceptionFactory.create(ex);
    s.printStackTrace();
    String message = String.format("Error during %s: %s", during, s.getMessage());
    TextComponentString txt = new TextComponentString(message);
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    owner.sendMessage(txt);
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {

    SpellModule.installInto(env, getConverters(), spellEntity);
    EntitiesModule.installInto(env, getConverters(), spellEntity);

    dependencies.installModules(env, executor, stateContext);

    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    executor.call(MAIN, stateContext, commandLineFunc);
  }

  private void installSystemLibraries() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    FileSystem fileSystem = runtimeEnv.fileSystem();
    LuaFunctionBinaryCache luaFunctionCache = context.getLuaFunctionBinaryCache();

    BasicLib.installInto(stateContext, env, /* runtimeEnv */ null, loader);

    // We don't pass the loader to the ModuleLib in order to prevent the installation of the
    // ChunkLoadPathSearcher
    ModuleLib.installInto(stateContext, env, /* runtimeEnv */ null, /* loader */ null,
        /* classLoader */ null);
    // Instead we install our own two searchers
    ClasspathResourceSearcher.installInto(env, loader, luaFunctionCache, classLoader);
    PatchedChunkLoadPathSearcher.installInto(env, loader, luaFunctionCache, classLoader, fileSystem,
        new PatchedChunkLoadPathSearcher.Context() {
          @Override
          public String getLuaPath() {
            return defaultLuaPath;
          }
        });

    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);
  }

  public void replacePlayerInstance(EntityPlayerMP player) {
    if (this.owner.getCommandSenderEntity() instanceof EntityPlayer) {
      if (this.owner.getCommandSenderEntity().getUniqueID().equals(player.getUniqueID())) {
        this.owner = player;
      }
    }
    PlayerClass playerClass = luaClassLoader.getLuaClassOfType(PlayerClass.class);
    playerClass.replaceDelegate(player);
  }
}
