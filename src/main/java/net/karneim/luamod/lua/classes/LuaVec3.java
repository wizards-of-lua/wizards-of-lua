package net.karneim.luamod.lua.classes;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

public class LuaVec3 {

  public static final String MODULE = LuaVec3.class.getPackage().getName() + ".Vec3";

  public static Table META_TABLE(Table env) {
    Table result = (Table) env.rawget("Vec3");
    result.rawset("__index", result);
    return result;
  }
  
  public static LuaFunction FROM(Table env) {
    Table metatable = META_TABLE(env);
    LuaFunction result = (LuaFunction) metatable.rawget("from");
    return result;
  }

}
