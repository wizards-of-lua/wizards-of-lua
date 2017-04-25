package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

public interface LuaWrapper<J, L> {
  default @Nullable L createLuaObjectNullable(@Nullable J javaObject) {
    if (javaObject == null) {
      return null;
    }
    return createLuaObject(javaObject);
  }

  L createLuaObject(J javaObject);
}
