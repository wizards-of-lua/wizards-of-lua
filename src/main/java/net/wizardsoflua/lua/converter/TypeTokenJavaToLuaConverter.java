package net.wizardsoflua.lua.converter;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;

public abstract class TypeTokenJavaToLuaConverter<J> implements JavaToLuaConverter<J> {
  private @Nullable Class<J> javaClass;

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
}
