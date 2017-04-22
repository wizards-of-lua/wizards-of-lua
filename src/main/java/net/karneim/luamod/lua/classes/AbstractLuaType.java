package net.karneim.luamod.lua.classes;

import com.google.common.base.Preconditions;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public abstract class AbstractLuaType implements LuaType {

  private LuaTypesRepo repo;

  @Override
  public void setRepo(LuaTypesRepo repo) {
    this.repo = repo;
  }

  @Override
  public LuaTypesRepo getRepo() {
    Preconditions.checkState(repo != null, "repo is null!");
    return repo;
  }

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc = loader.loadTextChunk(new Variable(getRepo().getEnv()), getTypeName(),
        String.format("require \"%s\"", getModule()));
    executor.call(state, classFunc);
    addFunctions();
  }

  protected abstract void addFunctions();
}
