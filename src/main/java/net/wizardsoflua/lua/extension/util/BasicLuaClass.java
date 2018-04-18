package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;

public abstract class BasicLuaClass<J, I extends Delegator<? extends J>>
    extends CachingLuaClass<J, Delegator<? extends I>> {
  @Resource
  private LuaConverters converters;

  private @Nullable Class<I> instanceClass;

  public Class<I> getInstanceClass() {
    if (instanceClass == null) {
      TypeToken<I> token = new TypeToken<I>(getClass()) {
        private static final long serialVersionUID = 1L;
      };
      @SuppressWarnings("unchecked")
      Class<I> result = (Class<I>) token.getRawType();
      instanceClass = result;
    }
    return instanceClass;
  }

  @AfterInjection
  public void registerInstanceConverter() {
    converters.registerLuaConverter(new TypeTokenLuaConverter<I, Delegator<? extends I>>() {
      @Override
      public Class<I> getJavaClass() {
        return getInstanceClass();
      }

      @Override
      public I getJavaInstance(Delegator<? extends I> luaInstance) {
        return luaInstance.getDelegate();
      }

      @Override
      public Delegator<? extends I> getLuaInstance(I javaInstance) {
        return BasicLuaClass.this.getLuaInstance(javaInstance.getDelegate());
      }
    });
  }

  @Override
  public J getJavaInstance(Delegator<? extends I> luaInstance) {
    I instance = luaInstance.getDelegate();
    J result = instance.getDelegate();
    return result;
  }
}
