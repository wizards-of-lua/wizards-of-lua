package net.karneim.luamod.lua.wrapper;

import net.sandius.rembulan.Table;

public class Metatables {

  public static Table get(Table env, String classname) {
    Table result = (Table) env.rawget(classname);
    return result;
  }

}
