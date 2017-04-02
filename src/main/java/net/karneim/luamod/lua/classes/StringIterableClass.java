package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.StringIterableInstance;

public class StringIterableClass {

  private static final StringIterableClass SINGLETON = new StringIterableClass();

  public static StringIterableClass get() {
    return SINGLETON;
  }

  public StringIterableInstance newInstance(LuaTypesRepo repo, Iterable<String> delegate) {
    return new StringIterableInstance(repo, delegate, null);
  }

}
