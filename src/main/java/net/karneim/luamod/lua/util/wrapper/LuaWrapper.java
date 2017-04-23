package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

public interface LuaWrapper<J, L> {
  default L toLuaObjectNullable(@Nullable J javaObject) {
    if (javaObject == null) {
      return null;
    }
    return toLuaObject(javaObject);
  }

  abstract L toLuaObject(J javaObject);
}
