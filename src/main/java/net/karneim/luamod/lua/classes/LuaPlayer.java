package net.karneim.luamod.lua.classes;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

public class LuaPlayer {

  public static final String MODULE = LuaPlayer.class.getPackage().getName() + ".Player";

  public static Table META_TABLE(Table env) {
    Table result = (Table) env.rawget("Player");
    return result;
  }
  
  public static LuaFunction NEW(Table env) {
    Table metatable = META_TABLE(env);
    LuaFunction result = (LuaFunction) metatable.rawget("new");
    return result;
  }

}
