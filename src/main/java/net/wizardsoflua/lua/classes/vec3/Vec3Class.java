package net.wizardsoflua.lua.classes.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

public class Vec3Class {
  public static final String METATABLE_NAME = "Vec3";

  private final Converters converters;
  private final Table metatable;

  public Vec3Class(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
  }

  public Table toLua(Vec3d delegate) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);

    builder.setMetatable(metatable);

    return builder.build();
  }

  public Vec3d toJava(Object luaObj) {
    converters.getTypes().checkAssignable(METATABLE_NAME, luaObj);
    Table luaTable = converters.castToTable(luaObj);
    double x = Conversions.floatValueOf(luaTable.rawget("x"));
    double y = Conversions.floatValueOf(luaTable.rawget("y"));
    double z = Conversions.floatValueOf(luaTable.rawget("z"));
    return new Vec3d(x, y, z);
  }

}
