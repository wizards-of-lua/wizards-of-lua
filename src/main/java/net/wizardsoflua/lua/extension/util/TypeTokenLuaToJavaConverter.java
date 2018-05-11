package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;

public abstract class TypeTokenLuaToJavaConverter<J, L> implements LuaToJavaConverter<J, L> {
  private @Nullable Class<J> javaClass;
  private @Nullable Class<L> luaClass;

  @Override
  public Class<J> getJavaClass() {
    if (javaClass == null) {
      TypeToken<J> token = new TypeToken<J>(getClass()) {
        private static final long serialVersionUID = 1L;
      };
      @SuppressWarnings("unchecked")
      Class<J> result = (Class<J>) token.getRawType();
      javaClass = result;
    }
    return javaClass;
  }

  @Override
  public Class<L> getLuaClass() {
    if (luaClass == null) {
      TypeToken<L> token = new TypeToken<L>(getClass()) {
        private static final long serialVersionUID = 1L;
      };
      @SuppressWarnings("unchecked")
      Class<L> result = (Class<L>) token.getRawType();
      luaClass = result;
    }
    return luaClass;
  }
}
