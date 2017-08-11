package net.wizardsoflua.lua.wrapper.vec3;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class Vec3Wrapper {

  private final WrapperFactory wrappers;
  private final Table luaTable;

  public Vec3Wrapper(WrapperFactory wrappers, Vec3d delegate) {
    this.wrappers = wrappers;
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);

    builder.setMetatable((Table) wrappers.getEnv().rawget("Vec3"));

    luaTable = builder.build();
  }

  public Table getLuaTable() {
    return luaTable;
  }
}
