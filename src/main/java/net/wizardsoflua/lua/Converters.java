package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.MapMaker;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.block.BlockClass;
import net.wizardsoflua.lua.classes.block.MaterialClass;
import net.wizardsoflua.lua.classes.entity.CreatureClass;
import net.wizardsoflua.lua.classes.entity.EntityClass;
import net.wizardsoflua.lua.classes.entity.MobClass;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.classes.spell.SpellClass;
import net.wizardsoflua.lua.classes.vec3.Vec3Class;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.spell.SpellEntity;

public class Converters extends WolConversions {
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

  public Types getTypes() {
    return types;
  }

  @SuppressWarnings("unchecked")
  public <T> T toJava(Class<T> type, Object luaObj) throws ConversionException {
    if (Vec3d.class == type) {
      Vec3d v = vec3Class.toJava(castToTable(luaObj));
      return (T) v;
    }
    if (WolBlock.class == type) {
      WolBlock v = blockClass.toJava(castToTable(luaObj));
      return (T) v;
    }
    return super.toJava(type, luaObj);
  }

  @Override
  public <T> Object toLua(T value) throws ConversionException {
    if (value instanceof Vec3d) {
      return vec3Class.toLua((Vec3d) value);
    }
    if (value instanceof WolBlock) {
      return blockClass.toLua((WolBlock) value);
    }
    if (value instanceof Material) {
      Material material = (Material) value;
      return cache.computeIfAbsent(material, t -> {
        return materialClass.toLua(material);
      });
    }
    if (value instanceof Entity) {
      Entity entity = (Entity) value;
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
    if (value instanceof Enum) {
      Enum<?> vEnum = (Enum<?>) value;
      if (vEnum instanceof IStringSerializable) {
        ByteString.of(((IStringSerializable) vEnum).getName());
      }
      return ByteString.of(vEnum.name());
    }
    if (value instanceof IStringSerializable) {
      return ByteString.of(((IStringSerializable) value).getName());
    }
    return super.toLua(value);
  }

}
