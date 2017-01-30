package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Preconditions;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;

public class NBTTagUtil {

  public static NBTTagCompound merge(NBTTagCompound origTagCompound, Table data) {
    NBTTagCompound resultTagCompound = new NBTTagCompound();
    Set<String> keys = origTagCompound.getKeySet();
    for (String key : keys) {
      Object luaValue = data.rawget(key);
      if (luaValue == null && isANumber(key)) {
        double dval = Double.parseDouble(key);
        luaValue = data.rawget(dval);
      }
      NBTBase oldValue = origTagCompound.getTag(key);
      if (luaValue != null) {
        NBTBase newValue = merge(oldValue, luaValue);
        resultTagCompound.setTag(key, newValue);
      } else {
        resultTagCompound.setTag(key, oldValue.copy());
      }
    }
    return resultTagCompound;
  }

  private static NBTTagList merge(NBTTagList origTagList, Table data) {
    NBTTagList resultTagList = origTagList.copy();
    int size = origTagList.tagCount();
    for (int i = 0; i < size; ++i) {
      int luaIdx = i + 1;
      Object luaValue = data.rawget(luaIdx);
      NBTBase oldValue = origTagList.get(i);
      if (luaValue != null) {
        NBTBase newValue = merge(oldValue, luaValue);
        resultTagList.set(i, newValue);
      } else {
        resultTagList.set(i, oldValue.copy());
      }
    }
    return resultTagList;
  }

  private static NBTBase merge(NBTBase tag, Object value) {
    if (tag == null) {
      // ignore
      return null;
    } else if (value == null) {
      return null;
    } else if (tag instanceof NBTTagEnd) {
      // ignore
      return tag;
    } else if (tag instanceof NBTTagFloat) {
      return new NBTTagFloat(((Number) value).floatValue());
    } else if (tag instanceof NBTTagDouble) {
      return new NBTTagDouble(((Number) value).doubleValue());
    } else if (tag instanceof NBTTagLong) {
      return new NBTTagLong(((Number) value).longValue());
    } else if (tag instanceof NBTTagInt) {
      return new NBTTagInt(((Number) value).intValue());
    } else if (tag instanceof NBTTagShort) {
      return new NBTTagShort(((Number) value).shortValue());
    } else if (tag instanceof NBTTagByte) {
      if (value instanceof Boolean) {
        boolean b = ((Boolean) value).booleanValue();
        return new NBTTagByte(b ? ((byte) 1) : ((byte) 0));
      }
      return new NBTTagByte(((Number) value).byteValue());
    } else if (tag instanceof NBTTagString) {
      if (value instanceof ByteString) {
        String s = ((ByteString) value).decode();
        return new NBTTagString(s);
      } else if (value instanceof String) {
        return new NBTTagString((String) value);
      } else {
        throw new IllegalArgumentException(
            "Expected a string but got " + (value.getClass().getSimpleName()));
      }
    } else if (tag instanceof NBTTagByteArray) {
      // Do we need that? Currently not supported!
      // return toTable(((NBTTagByteArray)tag).getByteArray());
      throw new UnsupportedOperationException("Conversion of NBTTagByteArray is not supported!");
    } else if (tag instanceof NBTTagIntArray) {
      // Do we need that? Currently not supported!
      // return toTable(((NBTTagIntArray)tag).getIntArray());
      throw new UnsupportedOperationException("Conversion of NBTTagIntArray is not supported!");
    } else if (tag instanceof NBTTagList) {
      if (value instanceof Table) {
        return merge((NBTTagList) tag, (Table) value);
      } else {
        throw new IllegalArgumentException(
            "Expected a table but got " + (value.getClass().getSimpleName()));
      }
    } else if (tag instanceof NBTTagCompound) {
      if (value instanceof Table) {
        return merge((NBTTagCompound) tag, (Table) value);
      } else {
        throw new IllegalArgumentException(
            "Expected a table but got " + (value.getClass().getSimpleName()));
      }
    } else {
      throw new UnsupportedOperationException(
          "Conversion is not supported for " + tag.getClass().getSimpleName());
    }
  }



  private static boolean isANumber(String txt) {
    return NumberUtils.isNumber(txt);
  }

  public static NBTTagCompound fromTable(Table data) {
    NBTTagCompound result = new NBTTagCompound();
    Object key = data.initialKey();
    while (key != null) {
      NBTBase value = toTag(data.rawget(key));
      if (value != null) {
        result.setTag(String.valueOf(key), value);
      }
      key = data.successorKeyOf(key);
    }
    return result;
  }

  private static NBTBase toTag(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Table) {
      return fromTable((Table) value);
    }
    if (value instanceof Double) {
      return new NBTTagDouble((Double) value);
    }
    if (value instanceof Float) {
      return new NBTTagFloat((Float) value);
    }
    if (value instanceof Long) {
      return new NBTTagLong((Long) value);
    }
    if (value instanceof Integer) {
      return new NBTTagInt((Integer) value);
    }
    if (value instanceof Short) {
      return new NBTTagShort((Short) value);
    }
    if (value instanceof Byte) {
      return new NBTTagByte((Byte) value);
    }
    if (value instanceof Boolean) {
      boolean b = ((Boolean) value).booleanValue();
      return new NBTTagByte(b ? ((byte) 1) : ((byte) 0));
    }
    return null;
  }

  public static void insertValues(ImmutableTable.Builder builder, NBTTagCompound tagCompound) {
    checkNotNull(tagCompound, "tagCompound==null!");
    Set<String> keys = tagCompound.getKeySet();
    for (String key : keys) {
      NBTBase tag = tagCompound.getTag(key);
      Object value = convert(tag);
      if (value != null) {
        Object luaKey = LuaTypeConverter.luaValueOf(key);
        builder.add(luaKey, value);
      }
    }
  }

  public static Object convert(NBTBase tag) {
    if (tag == null) {
      // ignore
      return null;
    } else if (tag instanceof NBTTagEnd) {
      // ignore
      return null;
    } else if (tag instanceof NBTTagFloat) {
      return ((NBTTagFloat) tag).getDouble();
    } else if (tag instanceof NBTTagDouble) {
      return ((NBTTagDouble) tag).getDouble();
    } else if (tag instanceof NBTPrimitive) {
      return ((NBTPrimitive) tag).getLong();
    } else if (tag instanceof NBTTagString) {
      return ByteString.of((((NBTTagString) tag).getString()));
    } else if (tag instanceof NBTTagByteArray) {
      // Do we need that? Currently not supported!
      // return toTable(((NBTTagByteArray)tag).getByteArray());
      throw new UnsupportedOperationException("Conversion of NBTTagByteArray is not supported!");
    } else if (tag instanceof NBTTagIntArray) {
      // Do we need that? Currently not supported!
      // return toTable(((NBTTagIntArray)tag).getIntArray());
      throw new UnsupportedOperationException("Conversion of NBTTagIntArray is not supported!");
    } else if (tag instanceof NBTTagList) {
      return toTable(((NBTTagList) tag));
    } else if (tag instanceof NBTTagCompound) {
      return toTable(((NBTTagCompound) tag));
    } else {
      throw new UnsupportedOperationException(
          "Conversion is not supported for " + tag.getClass().getSimpleName());
    }
  }

  public static Table toTable(NBTTagCompound tagCompound) {
    checkNotNull(tagCompound, "tagCompound==null!");
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    insertValues(builder, tagCompound);
    return builder.build();
  }

  public static Table toTable(NBTTagList list) {
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    int size = list.tagCount();
    for (int i = 0; i < size; ++i) {
      NBTBase tag = list.get(i);
      Object value = convert(tag);
      if (value != null) {
        builder.add((long) (i + 1), value);
      }
    }
    return builder.build();
  }

  public static Table toTable(int[] intArray) {
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    for (int i = 0; i < intArray.length; ++i) {
      builder.add((long) (i + 1), intArray[i]);
    }
    return builder.build();
  }



}
