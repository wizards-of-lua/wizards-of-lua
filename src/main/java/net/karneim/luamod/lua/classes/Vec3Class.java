package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.util.table.DelegatingTable;
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
  protected void addProperties(DelegatingTable.Builder<? extends Vec3d> b, Vec3d delegate) {
    b.addReadOnly("x", () -> repo.wrap(delegate.xCoord));
    b.addReadOnly("y", () -> repo.wrap(delegate.yCoord));
    b.addReadOnly("z", () -> repo.wrap(delegate.zCoord));
  }

  @Override
  protected void addFunctions(Table luaClass) {}

  public LuaFunction FROM() {
    Table luaClassTable = getLuaClassTable();
    LuaFunction result = (LuaFunction) luaClassTable.rawget("from");
    return result;
  }
}
