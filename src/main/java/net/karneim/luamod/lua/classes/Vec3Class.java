package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

@LuaModule("Vec3")
public class Vec3Class extends DelegatingLuaClass<Vec3d> {
  public Vec3Class(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder, Vec3d javaObject) {
    builder.add("x", repo.wrap(javaObject.xCoord));
    builder.add("y", repo.wrap(javaObject.yCoord));
    builder.add("z", repo.wrap(javaObject.zCoord));
  }

  @Override
  protected void addFunctions(Table luaClass) {}

  public LuaFunction FROM() {
    Table luaClassTable = getLuaClassTable();
    LuaFunction result = (LuaFunction) luaClassTable.rawget("from");
    return result;
  }
}
