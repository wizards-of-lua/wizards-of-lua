package net.karneim.luamod.lua;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import net.karneim.luamod.cache.FileCache;
import net.karneim.luamod.cursor.EnumDirection;
import net.karneim.luamod.gist.GistRepo;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.compiler.CompilerChunkLoader;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StandardLibrary;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class LuaTest {

  StateContext state = StateContexts.newDefaultInstance();
  ChunkLoader modulesLoader = CompilerChunkLoader.of("dyn_load");
  Table env =
      StandardLibrary.in(RuntimeEnvironments.system()).withLoader(modulesLoader).installInto(state);
  ChunkLoader loader = CompilerChunkLoader.of("SomeRootClassPrefix");
  File tmpLuaDir = new File("tmp");
  FileCache fileCache = new FileCache(tmpLuaDir);
  GistRepo gistRepo = new GistRepo(fileCache);

  private String load(String filename) {
    InputStream in = LuaTest.class.getResourceAsStream(filename);
    try {
      return IOUtils.toString(in);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Before
  public void clearCache() throws IOException {
    fileCache.clear();
  }

  @Test
  public void test1() throws Exception {
    // Given:
    String program = load("test1.lua");
    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  @Test
  public void test2() throws Exception {
    // Given:
    String program = load("test2.lua");
    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  static class MoveFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      System.out.println("move: " + arg1 + "," + arg2);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  static class RotateFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      System.out.println("rotate: " + arg1);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  @Test
  public void test3() throws Exception {
    // Given:
    String program = load("test3.lua");
    env.rawset("move", new MoveFunction());
    env.rawset("rotate", new RotateFunction());

    for (EnumDirection e : EnumDirection.values()) {
      env.rawset(e.name(), e.name());
    }
    for (EnumFacing e : EnumFacing.values()) {
      env.rawset(e.name(), e.name());
    }
    for (Rotation e : Rotation.values()) {
      env.rawset(e.name(), e.name());
    }

    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  @Test
  public void test4() throws Exception {
    // Given:
    String program = load("test4.lua");
    Table mytable = new DefaultTable();
    mytable.rawset("A", 3);
    mytable.rawset("B", 7);
    env.rawset("mytable", mytable);
    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  @Test
  public void test5() throws Exception {
    // Given:
    String program = load("test5.lua");
    Table cursor = new DefaultTable();
    cursor.rawset("move", new MoveFunction());
    cursor.rawset("rotate", new RotateFunction());

    for (EnumDirection e : EnumDirection.values()) {
      env.rawset(e.name(), e.name());
    }
    for (EnumFacing e : EnumFacing.values()) {
      env.rawset(e.name(), e.name());
    }
    for (Rotation e : Rotation.values()) {
      env.rawset(e.name(), e.name());
    }

    env.rawset("cursor", cursor);
    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  @Test
  public void test6() throws Exception {
    // Given:
    String program = load("test6.lua");

    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }

  @Test
  public void test7() throws Exception {
    // Given:
    ModuleLib.installInto(state, env, RuntimeEnvironments.system(), modulesLoader,
        ClassLoader.getSystemClassLoader());
    GistSearcher.installInto(env, modulesLoader, gistRepo, null);
    String program = load("test7.lua");

    LuaFunction main = loader.loadTextChunk(new Variable(env), "SomeChunkName", program);

    // When:
    DirectCallExecutor.newExecutor().call(state, main);

    // Then:
  }
}
