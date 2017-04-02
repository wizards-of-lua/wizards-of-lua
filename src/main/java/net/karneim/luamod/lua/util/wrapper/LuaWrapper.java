package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.karneim.luamod.lua.classes.LuaTypesRepo;

public abstract class LuaWrapper<J, L> {
  private final LuaTypesRepo repo;
  protected final @Nullable J delegate;
  private @Nullable L luaObject;

  public LuaWrapper(LuaTypesRepo repo, @Nullable J delegate) {
    this.repo = Preconditions.checkNotNull(repo);
    this.delegate = delegate;
  }

  public LuaTypesRepo getRepo() {
    return repo;
  }

  public @Nullable J getJavaObject() {
    return delegate;
  }

  public @Nullable L getLuaObject() {
    if (delegate == null) {
      return null;
    }
    if (luaObject == null) {
      luaObject = toLuaObject();
    }
    return luaObject;
  }

  protected abstract L toLuaObject();
}
