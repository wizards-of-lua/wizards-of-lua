package net.wizardsoflua.lua.converters.block;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.Conversions;

public class McPropertyValueToLuaTypeConverter {

  public static Object luaValueOf(Object obj) {
    if ( obj instanceof IStringSerializable) {
      IStringSerializable s = (IStringSerializable)obj;
      return s.getName();
    }
    if (obj instanceof Enum) {
      Enum<?> e = (Enum<?>) obj;
      return e.name();
    }
    if (obj instanceof String) {
      String str = (String) obj;
      if ("true".equals(obj)) {
        return true;
      }
      if ("false".equals(obj)) {
        return false;
      }
      Object result = Ints.tryParse(str);
      if (result != null) {
        return result;
      }
      result = Doubles.tryParse(str);
      if (result != null) {
        return result;
      }
    }
    return Conversions.canonicalRepresentationOf(obj);
  }
}
