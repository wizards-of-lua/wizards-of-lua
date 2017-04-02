package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.EnumInstance;

// TODO should we make a lua type of this?
public class EnumClass {

  private static final EnumClass SINGLETON = new EnumClass();

  public static EnumClass get() {
    return SINGLETON;
  }

  public EnumInstance newInstance(LuaTypesRepo repo, Enum<?> delegate) {
    return new EnumInstance(repo, delegate);
  }

}
