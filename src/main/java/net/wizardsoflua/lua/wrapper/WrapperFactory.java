package net.wizardsoflua.lua.wrapper;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.wrapper.block.BlockWrapper;
import net.wizardsoflua.lua.wrapper.block.MaterialWrapper;
import net.wizardsoflua.lua.wrapper.entity.PlayerWrapper;
import net.wizardsoflua.lua.wrapper.spell.SpellWrapper;
import net.wizardsoflua.lua.wrapper.vec3.Vec3Wrapper;
import net.wizardsoflua.spell.SpellEntity;

public class WrapperFactory {
  private class Cache {
    private final Map<Object, SoftReference<Table>> content = new MapMaker().weakKeys().makeMap();

    public Table computeIfAbsent(Object key, Function<Object, Table> supplier) {
      SoftReference<Table> value = content.get(key);
      if (value == null || value.get() == null) {
        value = soft(supplier.apply(key));
        content.put(key, value);
      }
      return value.get();
    }

    private <T> SoftReference<T> soft(T wrapper) {
      SoftReference<T> z = new SoftReference<T>(wrapper);
      return z;
    }
  }

  private final Types types;
  private final Cache cache = new Cache();

  public WrapperFactory(Types types) {
    this.types = types;
  }

  // TODO use getTypes().getEnv()
  @Deprecated
  public Table getEnv() {
    return types.getEnv();
  }
  
  public Types getTypes() {
    return types;
  }

  public @Nullable Boolean unwrapBoolean(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkBoolean(luaObj);
    return ((Boolean) luaObj).booleanValue();
  }


  public @Nullable Table wrap(@Nullable Vec3d vec3d) {
    if (vec3d == null) {
      return null;
    }
    Vec3Wrapper wrapper = new Vec3Wrapper(this, vec3d);
    return wrapper.getLuaTable();
  }

  public @Nullable Vec3d unwrapVec3(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkAssignable(Vec3Wrapper.METATABLE_NAME, luaObj);
    Vec3d result = Vec3Wrapper.unwrap((Table) luaObj);
    return result;
  }

  public @Nullable ByteString wrap(@Nullable String str) {
    if (str == null) {
      return null;
    }
    return ByteString.of(str);
  }

  public @Nullable String unwrapString(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkString(luaObj);
    return String.valueOf(Conversions.stringValueOf(luaObj));
  }

  public @Nullable Table wrap(@Nullable IBlockState blockState) {
    if (blockState == null) {
      return null;
    }
    return new BlockWrapper(this, blockState).getLuaTable();
  }

  public @Nullable Table wrap(@Nullable Entity entity) {
    if (entity == null) {
      return null;
    }
    if (entity instanceof SpellEntity) {
      return cache.computeIfAbsent(entity, t -> {
        return new SpellWrapper(WrapperFactory.this, (SpellEntity) entity);
      });
    }
    if (entity instanceof EntityPlayer) {
      return cache.computeIfAbsent(entity, t -> {
        return new PlayerWrapper(WrapperFactory.this, (EntityPlayer) entity);
      });
    }
    return null;
  }

  public @Nullable Table wrap(@Nullable Material material) {
    if (material == null) {
      return null;
    }
    return new MaterialWrapper(this, material).getLuaTable();
  }

  public @Nullable ByteString wrap(@Nullable EnumPushReaction mobilityFlag) {
    if (mobilityFlag == null) {
      return null;
    }
    return ByteString.of(mobilityFlag.name());
  }


}
