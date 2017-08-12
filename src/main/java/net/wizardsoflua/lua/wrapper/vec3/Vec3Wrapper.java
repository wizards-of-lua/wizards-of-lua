package net.wizardsoflua.lua.wrapper.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class Vec3Wrapper {

  public static final String METATABLE_NAME = "Vec3";

  public static Vec3d unwrap(Table luaTable) {
    double x = Conversions.floatValueOf(luaTable.rawget("x"));
    double y = Conversions.floatValueOf(luaTable.rawget("y"));
    double z = Conversions.floatValueOf(luaTable.rawget("z"));
    return new Vec3d(x, y, z);
  }

  private final WrapperFactory wrappers;
  private final Table luaTable;

  // TODO no need for creating a class here. this can be done just by a function
  public Vec3Wrapper(WrapperFactory wrappers, Vec3d delegate) {
    this.wrappers = wrappers;
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);

    builder.setMetatable((Table) wrappers.getEnv().rawget(METATABLE_NAME));

    luaTable = builder.build();
  }

  public Table getLuaTable() {
    return luaTable;
  }


}
