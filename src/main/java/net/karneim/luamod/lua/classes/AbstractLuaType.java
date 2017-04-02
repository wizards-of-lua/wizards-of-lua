package net.karneim.luamod.lua.classes;

import com.google.common.base.Preconditions;

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

}
