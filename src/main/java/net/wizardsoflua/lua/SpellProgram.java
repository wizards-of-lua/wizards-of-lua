package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.CoroutineLib;
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.blocks.BlocksModule;
import net.wizardsoflua.lua.module.entities.EntitiesModule;
import net.wizardsoflua.lua.module.print.PrintRedirector;
import net.wizardsoflua.lua.module.searcher.ClasspathResourceSearcher;
import net.wizardsoflua.lua.module.spell.SpellModule;
import net.wizardsoflua.lua.module.time.Time;
import net.wizardsoflua.lua.module.time.TimeModule;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.module.types.TypesModule;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;

public class SpellProgram {
  private enum State {
    NEW, PAUSED, FINISHED
  }
  public interface Context {

    SchedulingContextFactory getSchedulingContextFactory();

    Time getTime();
    
    File getLibraryDir();

  }

  private static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final ICommandSender source;
  private final String code;
  private final Context context;
  private final DirectCallExecutor executor;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final SpellRuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;
  private final Types types;
  private final Converters converters;
  private final ModuleDependencies dependencies = new ModuleDependencies();

  private State state;
  private Continuation continuation;
  private SpellEntity spellEntity;

  SpellProgram(ICommandSender source, String code, Context context) {
    this.source = checkNotNull(source, "source==null!");;
    this.code = checkNotNull(code, "code==null!");
    this.context = checkNotNull(context, "context==null!");
    this.executor = DirectCallExecutor.newExecutor(context.getSchedulingContextFactory());
    stateContext = StateContexts.newDefaultInstance();
    env = stateContext.newTable();
    runtimeEnv = new SpellRuntimeEnvironment(context.getLibraryDir());
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory(ROOT_CLASS_PREFIX);
    installSystemLibraries();
    types = new Types(env);
    TypesModule.installInto(env, types);
    converters = new Converters(types);
    PrintRedirector.installInto(env, source);
    TimeModule.installInto(converters, context.getTime());
    BlocksModule.installInto(env, converters);

    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Globals"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.inspect"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Check"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Vec3"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Material"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Block"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Entity"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Spell"));
    dependencies.add(new ModuleDependency("net.wizardsoflua.lua.modules.Player"));

    state = State.NEW;
  }

  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = spellEntity;
  }

  public String getCode() {
    return code;
  }

  public boolean isTerminated() {
    return state == State.FINISHED;
  }

  public void terminate() {
    state = State.FINISHED;
  }

  public void resume() throws SpellException {
    try {
      switch (state) {
        case NEW:
          try {
            compileAndRun();
            state = State.FINISHED;
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            state = State.PAUSED;
          } catch (Exception e) {
            state = State.FINISHED;
            throw e;
          }
          break;
        case PAUSED:
          try {
            executor.resume(continuation);
            state = State.FINISHED;
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            state = State.PAUSED;
          } catch (Exception e) {
            state = State.FINISHED;
            throw e;
          }
          break;
        default:
          break;
      }
    } catch (Exception ex) {
      throw exceptionFactory.create(ex);
    }
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    dependencies.installModules(env, executor, stateContext);

    SpellModule.installInto(env, converters, spellEntity);
    EntitiesModule.installInto(env, converters, spellEntity);

    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    executor.call(stateContext, commandLineFunc);
  }

  private void installSystemLibraries() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    BasicLib.installInto(stateContext, env, runtimeEnv, loader);
    ModuleLib.installInto(stateContext, env, runtimeEnv, /* modulesLoader */ loader, classLoader);
    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);
    ClasspathResourceSearcher.installInto(env, loader, /* luaFunctionCache, */
        classLoader);
  }

}
