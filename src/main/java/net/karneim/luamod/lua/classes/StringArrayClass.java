package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.StringArrayInstance;
import net.sandius.rembulan.Table;

public class StringArrayClass {
  private static final StringArrayClass SINGLETON = new StringArrayClass();

  public static StringArrayClass get() {
    return SINGLETON;
  }

  public StringArrayInstance newInstance(Table env, String[] delegate) {
    return new StringArrayInstance(env, delegate, null);
  }

}
