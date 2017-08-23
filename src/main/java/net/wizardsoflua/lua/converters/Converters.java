package net.wizardsoflua.lua.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.converters.block.BlockConverter;
import net.wizardsoflua.lua.converters.block.MaterialConverter;
import net.wizardsoflua.lua.converters.entity.EntityConverter;
import net.wizardsoflua.lua.converters.entity.PlayerConverter;
import net.wizardsoflua.lua.converters.spell.SpellConverter;
import net.wizardsoflua.lua.converters.vec3.Vec3Converter;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.spell.SpellEntity;

public class Converters {
  private class Cache {
    private final Map<Object, SoftReference<Table>> content = new MapMaker().weakKeys().makeMap();

    public Table computeIfAbsent(Object key, Function<Object, Table> supplier) {
      SoftReference<Table> valueRef = content.get(key);
      if (valueRef == null || valueRef.get() == null) {
        valueRef = soft(supplier.apply(key));
        content.put(key, valueRef);
      }
      return valueRef.get();
    }

    private <T> SoftReference<T> soft(T value) {
      return new SoftReference<T>(value);
    }
  }

  private final Cache cache = new Cache();
  private final Types types;

  private final Vec3Converter vec3Converter;
  private final BlockConverter blockConverter;
  private final MaterialConverter materialConverter;
  private final EntityConverter entityConverter;
  private final PlayerConverter playerConverter;
  private final SpellConverter spellConverter;

  public Converters(Types types) {
    this.types = checkNotNull(types, "types==null!");
    vec3Converter = new Vec3Converter(this);
    blockConverter = new BlockConverter(this);
    materialConverter = new MaterialConverter(this);
    entityConverter = new EntityConverter(this);
    playerConverter = new PlayerConverter(this);
    spellConverter = new SpellConverter(this);
  }

  // TODO use getTypes().getEnv()
  @Deprecated
  public Table getEnv() {
    return types.getEnv();
  }

  public Types getTypes() {
    return types;
  }

  public @Nullable Boolean booleanToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkBoolean(luaObj);
    return ((Boolean) luaObj).booleanValue();
  }

  public @Nullable Table vec3ToLua(@Nullable Vec3d value) {
    if (value == null) {
      return null;
    }
    return vec3Converter.toLua(value);
  }

  public @Nullable Vec3d vec3ToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkAssignable(Vec3Converter.METATABLE_NAME, luaObj);
    Vec3d result = vec3Converter.toJava((Table) luaObj);
    return result;
  }

  public @Nullable ByteString stringToLua(@Nullable String str) {
    if (str == null) {
      return null;
    }
    return ByteString.of(str);
  }

  public @Nullable String stringToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkString(luaObj);
    return String.valueOf(Conversions.stringValueOf(luaObj));
  }

  public @Nullable Table blockToLua(@Nullable WolBlock block) {
    if (block == null) {
      return null;
    }
    return blockConverter.toLua(block);
  }

  public @Nullable WolBlock blockToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    return blockConverter.toJava(luaObj);
  }

  public @Nullable Table entityToLua(@Nullable Entity entity) {
    if (entity == null) {
      return null;
    }
    if (entity instanceof SpellEntity) {
      return cache.computeIfAbsent(entity, t -> {
        return spellConverter.toLua((SpellEntity) entity);
      });
    }
    if (entity instanceof EntityPlayer) {
      return cache.computeIfAbsent(entity, t -> {
        return playerConverter.toLua((EntityPlayer) entity);
      });
    }
    return cache.computeIfAbsent(entity, t -> {
      return entityConverter.toLua(entity);
    });
  }

  public @Nullable Table materialToLua(@Nullable Material material) {
    if (materialConverter == null) {
      return null;
    }
    return materialConverter.toLua(material);
  }

  public @Nullable ByteString enumToLua(@Nullable Enum<?> value) {
    if (value == null) {
      return null;
    }
    if (value instanceof IStringSerializable) {
      return enumToLua((IStringSerializable) value);
    }
    return ByteString.of(value.name());
  }

  public @Nullable ByteString enumToLua(@Nullable IStringSerializable value) {
    if (value == null) {
      return null;
    }
    return ByteString.of(((IStringSerializable) value).getName());
  }

}
