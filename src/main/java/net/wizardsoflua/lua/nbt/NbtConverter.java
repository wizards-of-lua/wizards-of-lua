package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.lua.table.TableIterable;

@SpellScoped
public class NbtConverter {
  private static final String DEFAULT_PATH = "nbt";
  private final LuaTypes types;
  private @Nullable Map<Class<? extends NBTBase>, NbtMerger<? extends NBTBase>> mergers;

  public NbtConverter(@Resource LuaTypes types) {
    this.types = requireNonNull(types, "types == null!");
  }

  private Map<Class<? extends NBTBase>, NbtMerger<? extends NBTBase>> getMergers() {
    if (mergers == null) {
      mergers = new HashMap<>();
      registerMerger(NBTTagByte.class, new NbtByteMerger(this));
      registerMerger(NBTTagCompound.class, new NbtCompoundMerger(this));
      registerMerger(NBTTagDouble.class, new NbtDoubleMerger(this));
      registerMerger(NBTTagFloat.class, new NbtFloatMerger(this));
      registerMerger(NBTTagInt.class, new NbtIntMerger(this));
      registerMerger(NBTTagList.class, new NbtListMerger(this));
      registerMerger(NBTTagLong.class, new NbtLongMerger(this));
      registerMerger(NBTTagShort.class, new NbtShortMerger(this));
      registerMerger(NBTTagString.class, new NbtStringMerger(this));
      registerMerger(NBTTagByteArray.class, new NbtByteArrayMerger(this));
    }
    return mergers;
  }

  private <NBT extends NBTBase> void registerMerger(Class<NBT> cls, NbtMerger<NBT> merger) {
    if (getMergers().containsKey(cls)) {
      throw new IllegalArgumentException("Duplicate merger for " + cls);
    }
    getMergers().put(cls, merger);
  }

  private <NBT extends NBTBase> NbtMerger<NBT> getMerger(Class<NBT> nbtType) {
    @SuppressWarnings("unchecked")
    NbtMerger<NBT> result = (NbtMerger<NBT>) getMergers().get(nbtType);
    checkArgument(result != null, "Unsupported NBT type", nbtType.getSimpleName());
    return result;
  }

  private String keyToString(Object luaKey, int index, String path) {
    ByteString result = Conversions.stringValueOf(luaKey);
    if (result == null) {
      String actualType = types.getLuaTypeNameOfLuaObject(luaKey);
      throw new ConversionException("Can't convert key " + index + " in " + path
          + "! string/number expected, but got " + actualType);
    }
    return result.toString();
  }

  ConversionException conversionException(String path, Object actual, String expected) {
    String actualType = types.getLuaTypeNameOfLuaObject(actual);
    return new ConversionException(
        "Can't convert " + path + "! " + expected + " expected, but got " + actualType);
  }

  public NBTTagCompound merge(NBTTagCompound nbt, Table data) {
    return merge(nbt, data, DEFAULT_PATH);
  }

  NBTTagCompound merge(NBTTagCompound nbt, Table data, String path) {
    NBTTagCompound result = nbt.copy();
    insert(result, data, path);
    return result;
  }

  public void insert(NBTTagCompound nbt, Table data) {
    insert(nbt, data, DEFAULT_PATH);
  }

  void insert(NBTTagCompound nbt, Table data, String path) {
    int i = 0;
    for (Entry<Object, Object> entry : new TableIterable(data)) {
      String key = keyToString(entry.getKey(), ++i, path);
      Object newLuaValue = entry.getValue();

      String entryPath = path + "." + key;
      NBTBase oldNbtValue = nbt.getTag(key);
      NBTBase newNbtValue;
      if (oldNbtValue != null) {
        newNbtValue = merge(oldNbtValue, newLuaValue, key, entryPath);
      } else {
        newNbtValue = toNbt(newLuaValue, entryPath);
      }
      nbt.setTag(key, newNbtValue);
    }
  }

  <NBT extends NBTBase> NBT merge(NBT nbt, Object data, String key, String path) {
    checkNotNull(key, "key == null!");
    checkNotNull(nbt, "nbt == null!");
    checkNotNull(data, "data == null!");
    @SuppressWarnings("unchecked")
    Class<NBT> nbtType = (Class<NBT>) nbt.getClass();
    NbtMerger<NBT> converter = getMerger(nbtType);
    return converter.merge(nbt, data, key, path);
  }

  public static Object toLua(NBTBase nbt) {
    checkNotNull(nbt, "nbt == null!");
    if (nbt instanceof NBTPrimitive)
      return toLua((NBTPrimitive) nbt);
    if (nbt instanceof NBTTagString)
      return toLua((NBTTagString) nbt);
    if (nbt instanceof NBTTagList)
      return toLua((NBTTagList) nbt);
    if (nbt instanceof NBTTagCompound)
      return toLua((NBTTagCompound) nbt);
    if (nbt instanceof NBTTagIntArray)
      return toLua((NBTTagIntArray) nbt);
    if (nbt instanceof NBTTagByteArray)
      return toLua((NBTTagByteArray) nbt);
    throw new IllegalArgumentException(
        "Unsupported NBT type for conversion: " + nbt.getClass().getName());
  }

  public static Number toLua(NBTPrimitive nbt) {
    checkNotNull(nbt, "nbt == null!");
    if (nbt instanceof NBTTagDouble)
      return ((NBTTagDouble) nbt).getDouble();
    if (nbt instanceof NBTTagFloat)
      return ((NBTTagFloat) nbt).getDouble();
    return nbt.getLong();
  }

  public static ByteString toLua(NBTTagString nbt) {
    checkNotNull(nbt, "nbt == null!");
    return ByteString.of(nbt.getString());
  }

  public static Table toLua(NBTTagList nbt) {
    checkNotNull(nbt, "nbt == null!");
    Table result = new DefaultTable();
    for (int i = 0; i < nbt.tagCount(); i++) {
      NBTBase nbtValue = nbt.get(i);
      Object luaValue = toLua(nbtValue);
      result.rawset(i + 1, luaValue);
    }
    return result;
  }

  public static Table toLua(NBTTagCompound nbt) {
    checkNotNull(nbt, "nbt == null!");
    Table result = new DefaultTable();
    for (String key : nbt.getKeySet()) {
      NBTBase nbtValue = nbt.getTag(key);
      Object luaValue = toLua(nbtValue);
      result.rawset(key, luaValue);
    }
    return result;
  }

  public static Table toLua(NBTTagIntArray nbt) {
    checkNotNull(nbt, "nbt == null!");
    Table result = new DefaultTable();
    int[] arr = nbt.getIntArray();
    for (int i = 0; i < arr.length; ++i) {
      long key = i + 1;
      Object value = arr[i];
      result.rawset(key, value);
    }
    return result;
  }
  
  public static Table toLua(NBTTagByteArray nbt) {
    checkNotNull(nbt, "nbt == null!");
    Table result = new DefaultTable();
    byte[] arr = nbt.getByteArray();
    for (int i = 0; i < arr.length; ++i) {
      long key = i + 1;
      Object value = arr[i];
      result.rawset(key, value);
    }
    return result;
  }

  public NBTBase toNbt(Object data) {
    return toNbt(data, DEFAULT_PATH);
  }

  private NBTBase toNbt(Object data, String path) {
    checkNotNull(data, "data == null!");
    data = adjustType(data, path);
    if (data instanceof Boolean)
      return toNbt((Boolean) data);
    if (data instanceof Byte)
      return toNbt((Byte) data);
    if (data instanceof ByteString)
      return toNbt((ByteString) data);
    if (data instanceof Double)
      return toNbt((Double) data);
    if (data instanceof Float)
      return toNbt((Float) data);
    if (data instanceof Integer)
      return toNbt((Integer) data);
    if (data instanceof Long)
      return toNbt((Long) data);
    if (data instanceof Short)
      return toNbt((Short) data);
    if (data instanceof String)
      return toNbt((String) data);
    if (data instanceof Table)
      return toNbt((Table) data, path);
    throw new IllegalArgumentException(
        "Unsupported type for NBT conversion: " + data.getClass().getName());
  }

  /**
   * Workaround for <a href="https://bugs.mojang.com/browse/MC-112257">MC-112257</a>.
   */
  private Object adjustType(Object data, String path) {
    switch (path) {
      case DEFAULT_PATH + ".tag.RepairCost":
        data = toIntIfPossible(data);
        break;
    }
    return data;
  }

  /**
   * Convert {@code data} to an {@code int} if possible.
   *
   * @param data
   * @return an {@code int} or {@code data}
   */
  private static Object toIntIfPossible(Object data) {
    if (data instanceof Number) {
      Number number = (Number) data;
      int result = number.intValue();
      if (result == number.doubleValue()) {
        return result;
      }
    }
    return data;
  }

  public static NBTTagByte toNbt(boolean data) {
    return toNbt(data ? (byte) 1 : (byte) 0);
  }

  public static NBTTagByte toNbt(Boolean data) {
    return toNbt(data.booleanValue());
  }

  public static NBTTagByte toNbt(byte data) {
    return new NBTTagByte(data);
  }

  public static NBTTagByte toNbt(Byte data) {
    return toNbt(data.byteValue());
  }

  public static NBTTagString toNbt(ByteString data) {
    return toNbt(data.toString());
  }

  public static NBTTagDouble toNbt(double data) {
    return new NBTTagDouble(data);
  }

  public static NBTTagDouble toNbt(Double data) {
    return toNbt(data.doubleValue());
  }

  public static NBTTagFloat toNbt(float data) {
    return new NBTTagFloat(data);
  }

  public static NBTTagFloat toNbt(Float data) {
    return toNbt(data.floatValue());
  }

  public static NBTTagInt toNbt(int data) {
    return new NBTTagInt(data);
  }

  public static NBTTagInt toNbt(Integer data) {
    return toNbt(data.intValue());
  }

  public static NBTTagLong toNbt(long data) {
    return new NBTTagLong(data);
  }

  public static NBTTagLong toNbt(Long data) {
    return toNbt(data.longValue());
  }

  public static NBTTagShort toNbt(short data) {
    return new NBTTagShort(data);
  }

  public static NBTTagShort toNbt(Short data) {
    return toNbt(data.shortValue());
  }

  public static NBTTagString toNbt(String data) {
    return new NBTTagString(data);
  }

  public NBTBase toNbt(Table data) {
    return toNbt(data, DEFAULT_PATH);
  }

  private NBTBase toNbt(Table data, String path) {
    Table table = data;
    if (isArray(table)) {
      return toNbtList(table, path);
    } else {
      return toNbtCompound(table, path);
    }
  }

  /**
   * Try to guess if the given table is an array.
   *
   * @param table
   * @return true if we guess it's an array
   */
  private static boolean isArray(Table table) {
    long count = 0;
    for (Map.Entry<Object, Object> entry : new TableIterable(table)) {
      count++;
      Object key = entry.getKey();
      Long intValue = Conversions.integerValueOf(key);
      if (intValue == null) {
        return false;
      }
      if (intValue.longValue() != count) {
        return false;
      }
    }
    return count > 0;
  }

  public NBTTagList toNbtList(Table data) {
    return toNbtList(data, DEFAULT_PATH);
  }

  NBTTagList toNbtList(Table data, String path) {
    NBTTagList result = new NBTTagList();
    int i = 0;
    for (Entry<Object, Object> entry : new TableIterable(data)) {
      Object value = entry.getValue();
      NBTBase nbtValue = toNbt(value, path + '[' + (++i) + ']');
      result.appendTag(nbtValue);
    }
    return result;
  }

  public NBTTagCompound toNbtCompound(Table data) {
    return toNbtCompound(data, DEFAULT_PATH);
  }

  NBTTagCompound toNbtCompound(Table data, String path) {
    NBTTagCompound result = new NBTTagCompound();
    int i = 0;
    for (Entry<Object, Object> entry : new TableIterable(data)) {
      String key = keyToString(entry.getKey(), ++i, path);
      Object value = entry.getValue();
      NBTBase nbtValue = toNbt(value, path + '.' + key);
      result.setTag(key, nbtValue);
    }
    return result;
  }
}
