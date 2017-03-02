package net.karneim.luamod.lua;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityList;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class EntityMetaTables {

  public static void addTablesForEntityTypes(Table env) {
    for (String name : EntityList.CLASS_TO_NAME.values()) {
      System.out.println("name "+name);
      Table table = new DefaultTable();
      table.rawset("__index", table);
      env.rawset(name, table);
    }
  }

  public static @Nullable Table getMetaTable(Table env, Class<?> entityClass) {
    String name = EntityList.CLASS_TO_NAME.get(entityClass);
    Object result = env.rawget(name);
    if (result instanceof Table) {
      return (Table) result;
    } else {
      throw new IllegalStateException(String.format("Variable %s is not of type table!", name));
    }
  }
  
  public static @Nullable String getName(Class<?> entityClass) {
    return EntityList.CLASS_TO_NAME.get(entityClass);
  }

}
