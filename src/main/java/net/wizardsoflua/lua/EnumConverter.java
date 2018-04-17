package net.wizardsoflua.lua;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.GameType;

public class EnumConverter {

  private Map<Class<?>, Map<String, Object>> toJavaMapping = new HashMap<>();
  private Map<Class<?>, Map<Object, String>> toLuaMapping = new HashMap<>();

  @SuppressWarnings("unchecked")
  public <T> T toJava(Class<T> type, String name) {
    // TODO support all Enum types and IStringSerializable
    // if (IStringSerializable.class.isAssignableFrom(type)) {
    //
    // }

    Map<String, Object> map = getToJavaMapping(type);
    Object result = map.get(name);
    return (T) result;
  }

  public Object toLua(Enum<?> vEnum) {
    Map<Object, String> map = getToLuaMapping(vEnum);
    String result = map.get(vEnum);
    return result;
  }

  private Map<Object, String> getToLuaMapping(Enum<?> vEnum) {
    Map<Object, String> result = toLuaMapping.get(vEnum.getClass());
    if (result == null) {
      result = new HashMap<>();
      toLuaMapping.put(vEnum.getClass(), result);
      Class<?> type = vEnum.getClass();
      Object[] constants = type.getEnumConstants();
      for (Object c : constants) {
        Enum<?> e = (Enum<?>) c;
        String name = getName(e);
        result.put(e, name);
      }
    }
    return result;
  }

  private Map<String, Object> getToJavaMapping(Class<?> type) {
    Map<String, Object> result = toJavaMapping.get(type);
    if (result == null) {
      result = new HashMap<>();
      toJavaMapping.put(type, result);
      Object[] constants = type.getEnumConstants();
      for (Object c : constants) {
        Enum<?> e = (Enum<?>) c;
        String name = getName(e);
        result.put(name, e);
      }
    }
    return result;
  }

  private String getName(Enum<?> e) {
    if (e instanceof GameType) {
      return ((GameType) e).getName();
    }
    return e.name();
  }



}
