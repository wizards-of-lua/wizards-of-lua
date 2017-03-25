package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.classes.EnumClass;
import net.karneim.luamod.lua.classes.StringIterableClass;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class WrapperFactory {
  public static PatchedImmutableTable wrap(Table env, BlockPos delegate) {
    return Vec3Class.get().newInstance(env, delegate).getLuaObject();
  }

  public static ByteString wrap(Table env, Enum<?> delegate) {
    return EnumClass.get().newInstance(env, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Table env, Iterable<String> delegate) {
    return StringIterableClass.get().newInstance(env, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Table env, Vec3d delegate) {
    return Vec3Class.get().newInstance(env, delegate).getLuaObject();
  }
}
