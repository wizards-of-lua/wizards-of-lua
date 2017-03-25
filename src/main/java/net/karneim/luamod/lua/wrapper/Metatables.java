package net.karneim.luamod.lua.wrapper;

import net.sandius.rembulan.Table;

public class Metatables {

  private static final String OBJECT_METATABLES_VARIABLE_NAME = "_objectmetatables";

  public static Table get(Table env, String classname) {
    Table result = (Table) env.rawget(classname);
    return result;
  }

}
