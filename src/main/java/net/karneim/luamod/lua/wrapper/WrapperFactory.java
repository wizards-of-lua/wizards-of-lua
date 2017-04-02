package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.lua.classes.BlockStateClass;
import net.karneim.luamod.lua.classes.EntityClass;
import net.karneim.luamod.lua.classes.EntityLivingClass;
import net.karneim.luamod.lua.classes.EntityPlayerClass;
import net.karneim.luamod.lua.classes.EnumClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.StringIterableClass;
import net.karneim.luamod.lua.classes.Vec3Class;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;

public class WrapperFactory {

  public static PatchedImmutableTable wrap(LuaTypesRepo repo, BlockPos delegate) {
    return repo.get(Vec3Class.class).newInstance(delegate).getLuaObject();
  }

  public static ByteString wrap(LuaTypesRepo repo, Enum<?> delegate) {
    return EnumClass.get().newInstance(repo, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(LuaTypesRepo repo, Iterable<String> delegate) {
    return StringIterableClass.get().newInstance(repo, delegate).getLuaObject();
  }

  public static PatchedImmutableTable wrap(LuaTypesRepo repo, Vec3d delegate) {
    return repo.get(Vec3Class.class).newInstance(delegate).getLuaObject();
  }

  public static DelegatingTable wrap(LuaTypesRepo repo, IBlockState delegate) {
    return repo.get(BlockStateClass.class).newInstance(delegate).getLuaObject();
  }

  public static DelegatingTable wrap(LuaTypesRepo repo, Entity entity) {
    if (entity instanceof EntityLiving) {
      return repo.get(EntityLivingClass.class).newInstance((EntityLiving) entity).getLuaObject();
    }
    if (entity instanceof EntityPlayer) {
      return repo.get(EntityPlayerClass.class).newInstance((EntityPlayer) entity).getLuaObject();
    }
    return repo.get(EntityClass.class).newInstance(entity).getLuaObject();
  }

  // public static DelegatingTable wrapToArmor(LuaTypesRepo repo,
  // Iterable<ItemStack> itemStackList) {
  // return repo.get(ArmorClass.class).newInstance(itemStackList).getLuaObject();
  // }
}
