package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.classes.block.BlockClass;
import net.wizardsoflua.lua.classes.block.MaterialClass;
import net.wizardsoflua.lua.classes.entity.CreatureClass;
import net.wizardsoflua.lua.classes.entity.EntityClass;
import net.wizardsoflua.lua.classes.entity.MobClass;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.classes.spell.SpellClass;
import net.wizardsoflua.lua.classes.vec3.Vec3Class;
import net.wizardsoflua.lua.module.types.Terms;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.table.TableIterable;
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

  private final Vec3Class vec3Class;
  private final BlockClass blockClass;
  private final MaterialClass materialClass;
  private final EntityClass entityClass;
  private final CreatureClass creatureClass;
  private final MobClass mobClass;
  private final PlayerClass playerClass;
  private final SpellClass spellClass;

  public Converters(Types types) {
    this.types = checkNotNull(types, "types==null!");
    vec3Class = new Vec3Class(this);
    blockClass = new BlockClass(this);
    materialClass = new MaterialClass(this);
    entityClass = new EntityClass(this);
    creatureClass = new CreatureClass(this);
    mobClass = new MobClass(this);
    playerClass = new PlayerClass(this);
    spellClass = new SpellClass(this);
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
    return vec3Class.toLua(value);
  }

  // TODO add Terms parameter. Maybe join with Types implementation of castXXX
  public @Nullable Vec3d vec3ToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkAssignable(Vec3Class.METATABLE_NAME, luaObj);
    Vec3d result = vec3Class.toJava((Table) luaObj);
    return result;
  }

  public @Nullable ByteString stringToLua(@Nullable String str) {
    if (str == null) {
      return null;
    }
    return ByteString.of(str);
  }

  public @Nullable Table stringsToLua(@Nullable Iterable<String> strings) {
    if (strings == null) {
      return null;
    }
    DefaultTable result = new DefaultTable();
    int index = 0;
    for (String string : strings) {
      index++;
      result.rawset(index, stringToLua(string));
    }
    return result;
  }

  public @Nullable String stringToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    types.checkString(luaObj);
    return String.valueOf(Conversions.stringValueOf(luaObj));
  }

  public @Nullable Iterable<String> stringsToJava(Object luaObj, Terms terms) {
    Table table = types.castTable(luaObj, terms);
    if (table == null) {
      return null;
    }
    List<String> result = new ArrayList<>();
    TableIterable it = new TableIterable(table);
    for (Map.Entry<Object, Object> entry : it) {
      result.add(stringToJava(entry.getValue()));
    }
    return result;
  }

  public @Nullable Table blockToLua(@Nullable WolBlock block) {
    if (block == null) {
      return null;
    }
    return blockClass.toLua(block);
  }

  public @Nullable WolBlock blockToJava(@Nullable Object luaObj) {
    if (luaObj == null) {
      return null;
    }
    return blockClass.toJava(luaObj);
  }

  public @Nullable Table entityToLua(@Nullable Entity entity) {
    if (entity == null) {
      return null;
    }
    if (entity instanceof SpellEntity) {
      return cache.computeIfAbsent(entity, t -> {
        return spellClass.toLua((SpellEntity) entity);
      });
    }
    if (entity instanceof EntityPlayer) {
      return cache.computeIfAbsent(entity, t -> {
        return playerClass.toLua((EntityPlayer) entity);
      });
    }
    if (entity instanceof EntityLiving) {
      return cache.computeIfAbsent(entity, t -> {
        return mobClass.toLua((EntityLiving) entity);
      });
    }
    if (entity instanceof EntityLivingBase) {
      return cache.computeIfAbsent(entity, t -> {
        return creatureClass.toLua((EntityLivingBase) entity);
      });
    }
    return cache.computeIfAbsent(entity, t -> {
      return entityClass.toLua(entity);
    });
  }

  public @Nullable Table entitiesToLua(@Nullable Iterable<Entity> entities) {
    if (entities == null) {
      return null;
    }
    DefaultTable result = new DefaultTable();
    int index = 0;
    for (Entity entity : entities) {
      index++;
      result.rawset(index, entityToLua(entity));
    }
    return result;
  }

  public @Nullable Table materialToLua(@Nullable Material material) {
    if (materialClass == null) {
      return null;
    }
    return cache.computeIfAbsent(material, t -> {
      return materialClass.toLua(material);
    });
  }

  public @Nullable <E extends Enum<?>> ByteString enumToLua(@Nullable E value) {
    if (value == null) {
      return null;
    }
    if (value instanceof IStringSerializable) {
      return stringSerializableToLua((IStringSerializable) value);
    }
    return ByteString.of(value.name());
  }

  public @Nullable ByteString stringSerializableToLua(@Nullable IStringSerializable value) {
    if (value == null) {
      return null;
    }
    return ByteString.of(((IStringSerializable) value).getName());
  }


}
