package net.wizardsoflua.lua.classes.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

@DeclareLuaClass(name = Vec3Class.METATABLE_NAME)
public class Vec3Class extends JavaLuaClass<Vec3d, Table> {
  public static final String METATABLE_NAME = "Vec3";

  @Override
  public Table toLua(Vec3d javaObj) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", javaObj.xCoord);
    builder.add("y", javaObj.yCoord);
    builder.add("z", javaObj.zCoord);
    builder.setMetatable(getMetaTable());
    return builder.build();
  }

  @Override
  public Vec3d toJava(Table luaObj) {
    double x = Conversions.floatValueOf(luaObj.rawget("x"));
    double y = Conversions.floatValueOf(luaObj.rawget("y"));
    double z = Conversions.floatValueOf(luaObj.rawget("z"));
    return new Vec3d(x, y, z);
  }
}
