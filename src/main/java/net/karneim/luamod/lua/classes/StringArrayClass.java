package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.StringArrayInstance;

public class StringArrayClass {
  private static final StringArrayClass SINGLETON = new StringArrayClass();

  public static StringArrayClass get() {
    return SINGLETON;
  }

  public StringArrayInstance newInstance(LuaTypesRepo repo, String[] delegate) {
    return new StringArrayInstance(repo, delegate, null);
  }

}
