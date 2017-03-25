package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.EnumInstance;
import net.sandius.rembulan.Table;

public class EnumClass {

  private static final EnumClass SINGLETON = new EnumClass();

  public static EnumClass get() {
    return SINGLETON;
  }

  public EnumInstance newInstance(Table env, Enum<?> delegate) {
    return new EnumInstance(env, delegate);
  }

}
