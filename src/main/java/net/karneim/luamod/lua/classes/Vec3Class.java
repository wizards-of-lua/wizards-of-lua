package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.Metatables;
import net.karneim.luamod.lua.wrapper.Vec3Instance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

@LuaClass("Vec3")
public class Vec3Class extends AbstractLuaType {
  public Vec3Instance newInstance(Vec3d delegate) {
    return new Vec3Instance(getRepo(), delegate, Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  public Vec3Instance newInstance(BlockPos delegate) {
    Vec3d vec3d = new Vec3d(delegate);
    return new Vec3Instance(getRepo(), vec3d, Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}

  public LuaFunction FROM() {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    LuaFunction result = (LuaFunction) metatable.rawget("from");
    return result;
  }

}
