package net.wizardsoflua.lua.classes.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

@DeclareLuaClass(name = Vec3Class.METATABLE_NAME)
public class Vec3Class extends LuaClass<Vec3d> {
  public static final String METATABLE_NAME = "Vec3";

  public Vec3Class() {
    super(Vec3d.class);
  }

  @Override
  public Table toLua(Vec3d javaObj) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", javaObj.xCoord);
    builder.add("y", javaObj.yCoord);
    builder.add("z", javaObj.zCoord);
    builder.setMetatable(getMetatable());
    return builder.build();
  }

  @Override
  public Vec3d toJava(Table luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    Table luaTable = getConverters().castToTable(luaObj);
    double x = Conversions.floatValueOf(luaTable.rawget("x"));
    double y = Conversions.floatValueOf(luaTable.rawget("y"));
    double z = Conversions.floatValueOf(luaTable.rawget("z"));
    return new Vec3d(x, y, z);
  }


}
