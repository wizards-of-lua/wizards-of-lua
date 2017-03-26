package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.classes.BlockStateClass;
import net.karneim.luamod.lua.classes.EnumClass;
import net.karneim.luamod.lua.classes.StringIterableClass;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class WrapperFactory {
  private static final EntityWrapperFactory entityWrapperFactory = new EntityWrapperFactory();
  
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
  
  public static DelegatingTable wrap(Table env, IBlockState delegate) {
    return BlockStateClass.get().newInstance(env, delegate).getLuaObject();
  }
  
  public static DelegatingTable wrap(Table env, Entity delegate) {
    return entityWrapperFactory.create(env, delegate).getLuaObject();
  }
}
