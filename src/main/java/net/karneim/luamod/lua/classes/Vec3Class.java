package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

@LuaModule("Vec3")
public class Vec3Class extends ImmutableLuaClass<Vec3d> {
  public Vec3Class(Table env) {
    super(env);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder, Vec3d javaObject) {
    builder.add("x", javaObject.xCoord);
    builder.add("y", javaObject.yCoord);
    builder.add("z", javaObject.zCoord);
  }

  @Override
  protected void addFunctions(Table luaClass) {}

  public LuaFunction FROM() {
    Table luaClassTable = getLuaClassTable();
    LuaFunction result = (LuaFunction) luaClassTable.rawget("from");
    return result;
  }
}
