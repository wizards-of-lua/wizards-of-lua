package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class WrapperFactory {
  public static PatchedImmutableTable wrap(Table env, BlockPos delegate) {
    return new BlockPosWrapper(env, delegate).getLuaObject();
  }

  public static ByteString wrap(Table env, Enum<?> delegate) {
    return new EnumWrapper(env, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Table env, Iterable<String> delegate) {
    return new StringIterableWrapper(env, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Table env, Vec3d delegate) {
    return new Vec3Wrapper(env, delegate).getLuaObject();
  }
}
