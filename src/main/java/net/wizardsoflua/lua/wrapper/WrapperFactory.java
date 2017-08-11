package net.wizardsoflua.lua.wrapper;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.entity.PlayerWrapper;
import net.wizardsoflua.lua.wrapper.spell.SpellWrapper;
import net.wizardsoflua.lua.wrapper.vec3.Vec3Wrapper;
import net.wizardsoflua.spell.SpellEntity;

public class WrapperFactory {
  private final Cache cache = new Cache();
  private final Table env;

  public WrapperFactory(Table env) {
    this.env = env;
  }

  public Table getEnv() {
    return env;
  }

  public @Nullable Table wrap(@Nullable Vec3d vec3d) {
    if (vec3d == null) {
      return null;
    }
    Vec3Wrapper wrapper = new Vec3Wrapper(this, vec3d);
    return wrapper.getLuaTable();
  }

  public @Nullable ByteString wrap(@Nullable String str) {
    if (str == null) {
      return null;
    }
    return ByteString.of(str);
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
}
