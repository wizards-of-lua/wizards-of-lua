package net.karneim.luamod.lua.classes;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

public class LuaEntity {

  public static final String MODULE = LuaEntity.class.getPackage().getName() + ".Entity";

  public static Table META_TABLE(Table env) {
    Table result = (Table) env.rawget("Entity");
    return result;
  }
  
  public static LuaFunction NEW(Table env) {
    Table metatable = META_TABLE(env);
    LuaFunction result = (LuaFunction) metatable.rawget("new");
    return result;
  }

}
