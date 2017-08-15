package net.wizardsoflua.lua.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.wrapper.block.BlockWrapper;
import net.wizardsoflua.lua.wrapper.block.MaterialWrapper;
import net.wizardsoflua.lua.wrapper.entity.EntityWrapper;
import net.wizardsoflua.lua.wrapper.entity.PlayerWrapper;
import net.wizardsoflua.lua.wrapper.spell.SpellWrapper;
import net.wizardsoflua.lua.wrapper.vec3.Vec3Wrapper;
import net.wizardsoflua.spell.SpellEntity;

public class Wrappers {
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

  private final Cache cache = new Cache();
  private final Types types;

  private final Vec3Wrapper vec3Wrapper;
  private final BlockWrapper blockWraper;
  private final MaterialWrapper materialWrapper;
  private final EntityWrapper entityWrapper;
  private final PlayerWrapper playerWrapper;
  private final SpellWrapper spellWrapper;

  public Wrappers(Types types) {
    this.types = checkNotNull(types, "types==null!");
    vec3Wrapper = new Vec3Wrapper(this);
    blockWraper = new BlockWrapper(this);
    materialWrapper = new MaterialWrapper(this);
    entityWrapper = new EntityWrapper(this);
    playerWrapper = new PlayerWrapper(this);
    spellWrapper = new SpellWrapper(this);
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

  public @Nullable Table wrap(@Nullable Vec3d value) {
    if (value == null) {
      return null;
    }
    return vec3Wrapper.wrap(value);
  }

  public @Nullable Vec3d unwrapVec3(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkAssignable(Vec3Wrapper.METATABLE_NAME, luaObj);
    Vec3d result = vec3Wrapper.unwrap((Table) luaObj);
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
    return blockWraper.wrap(blockState);
  }

  public @Nullable Table wrap(@Nullable Entity entity) {
    if (entity == null) {
      return null;
    }
    if (entity instanceof SpellEntity) {
      return cache.computeIfAbsent(entity, t -> {
        return spellWrapper.wrap((SpellEntity) entity);
      });
    }
    if (entity instanceof EntityPlayer) {
      return cache.computeIfAbsent(entity, t -> {
        return playerWrapper.wrap((EntityPlayer) entity);
      });
    }
    return cache.computeIfAbsent(entity, t -> {
      return entityWrapper.wrap(entity);
    });
  }

  public @Nullable Table wrap(@Nullable Material material) {
    if (materialWrapper == null) {
      return null;
    }
    return materialWrapper.wrap(material);
  }

  public @Nullable ByteString wrap(@Nullable Enum<?> value) {
    if (value == null) {
      return null;
    }
    if ( value instanceof IStringSerializable) {
      return wrap((IStringSerializable)value);
    }
    return ByteString.of(value.name());
  }
  
  public @Nullable ByteString wrap(@Nullable IStringSerializable value) {
    if (value == null) {
      return null;
    }
    return ByteString.of(((IStringSerializable) value).getName());
  }

}
