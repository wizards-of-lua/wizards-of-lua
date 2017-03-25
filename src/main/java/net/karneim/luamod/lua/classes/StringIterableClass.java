package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.StringIterableInstance;
import net.sandius.rembulan.Table;

public class StringIterableClass {

  private static final StringIterableClass SINGLETON = new StringIterableClass();

  public static StringIterableClass get() {
    return SINGLETON;
  }

  public StringIterableInstance newInstance(Table env, Iterable<String> delegate) {
    return new StringIterableInstance(env, delegate, null);
  }

}
