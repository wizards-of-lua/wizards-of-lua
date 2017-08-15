package net.wizardsoflua.lua.converters.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

public class Vec3Converters {
  public static final String METATABLE_NAME = "Vec3";

  private final Table metatable;

  public Vec3Converters(Converters converters) {
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

  public Vec3d unwrap(Table luaTable) {
    double x = Conversions.floatValueOf(luaTable.rawget("x"));
    double y = Conversions.floatValueOf(luaTable.rawget("y"));
    double z = Conversions.floatValueOf(luaTable.rawget("z"));
    return new Vec3d(x, y, z);
  }

}
