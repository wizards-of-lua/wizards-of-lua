package net.wizardsoflua.lua.wrapper;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.block.BlockStateWrapper;
import net.wizardsoflua.lua.wrapper.entity.PlayerWrapper;
import net.wizardsoflua.lua.wrapper.spell.SpellWrapper;
import net.wizardsoflua.lua.wrapper.vec3.Vec3Wrapper;
import net.wizardsoflua.spell.SpellEntity;

public class WrapperFactory {
  private static final String NIL_META = "nil";
  private static final String BOOLEAN_META = "boolean";
  private static final String NUMBER_META = "number";
  private static final String STRING_META = "string";
  private static final String TABLE_META = "table";
  
  private static final String CLASSNAME_META_KEY = "__classname";
  private final Cache cache = new Cache();
  private final Table env;

  public WrapperFactory(Table env) {
    this.env = env;
  }

  public Table getEnv() {
    return env;
  }
  
  public @Nullable Boolean unwrapBoolean(@Nullable Object luaObj) {
    if ( luaObj == null) {
      return null;
    }
    checkAssignable(BOOLEAN_META, luaObj);
    return ((Boolean)luaObj).booleanValue();
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
    checkAssignable(Vec3Wrapper.METATABLE_NAME, luaObj);
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
    checkAssignable(STRING_META, luaObj);
    return String.valueOf(Conversions.stringValueOf(luaObj));
  }

  public @Nullable Table wrap(@Nullable IBlockState blockState) {
    if (blockState == null) {
      return null;
    }
    return new BlockStateWrapper(this, blockState).getLuaTable();
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

  private void checkAssignable(String expectedMetatableName, Object luaObj) {
    if (!isAssignable(expectedMetatableName, luaObj)) {
      throw new IllegalArgumentException(
          String.format("Expected %s but got %s", expectedMetatableName, getTypeOf(luaObj)));
    }
  }

  private boolean isAssignable(String expectedMetatableName, Object luaObj) {
    if (luaObj == null) {
      return true;
    }
    if (BOOLEAN_META.equals(expectedMetatableName)) {
      return luaObj instanceof Boolean;
    }
    if (NUMBER_META.equals(expectedMetatableName)) {
      return Conversions.numericalValueOf(luaObj) != null;
    }
    if (STRING_META.equals(expectedMetatableName)) {
      return Conversions.stringValueOf(luaObj) != null;
    }
    if (TABLE_META.equals(expectedMetatableName)) {
      return !(luaObj instanceof Table);
    }
    if (luaObj instanceof Table) {
      Table actualMetatable = ((Table) luaObj).getMetatable();
      if (actualMetatable != null) {
        return isAssignable(expectedMetatableName, actualMetatable);
      }
    }
    return false;
  }

  private boolean isAssignable(String expectedMetatableName, Table actualMetatable) {
    Table expectedMetatable = (Table) env.rawget(expectedMetatableName);
    Table currentMetatable = actualMetatable;
    while (currentMetatable != null) {
      if (currentMetatable == expectedMetatable) {
        return true;
      }
      currentMetatable = currentMetatable.getMetatable();
    }
    return false;
  }

  private String getTypeOf(Object luaObj) {
    if (luaObj == null) {
      return NIL_META;
    }
    if (luaObj instanceof Table) {
      Table actualMetatable = ((Table) luaObj).getMetatable();
      if (actualMetatable == null) {
        return TABLE_META;
      }
      String actualTypeName = (String) actualMetatable.rawget(CLASSNAME_META_KEY);
      if (actualTypeName == null) {
        return TABLE_META;
      }
      return actualTypeName;
    }
    if (luaObj instanceof Number) {
      return NUMBER_META;
    }
    if (luaObj instanceof Boolean) {
      return BOOLEAN_META;
    }
    if (luaObj instanceof ByteString || luaObj instanceof String) {
      return STRING_META;
    }
    // Fallback
    return luaObj.getClass().getSimpleName();
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
