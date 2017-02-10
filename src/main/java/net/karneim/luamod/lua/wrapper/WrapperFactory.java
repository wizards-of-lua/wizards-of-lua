package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;

public class WrapperFactory {
  public static PatchedImmutableTable wrap(BlockPos delegate) {
    return new BlockPosWrapper(delegate).getLuaObject();
  }

  public static ByteString wrap(Enum<?> delegate) {
    return new EnumWrapper(delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Iterable<String> delegate) {
    return new StringIterableWrapper(delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(Vec3d delegate) {
    return new Vec3dWrapper(delegate).getLuaObject();
  }
}
