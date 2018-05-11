package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.common.Delegator;

@SuppressWarnings("serial")
public abstract class BasicLuaClass<J, I extends Delegator<? extends J>>
    extends CachingLuaClass<J, Delegator<? extends I>> {
  private @Nullable Class<J> javaClass;

  @Override
  public Class<J> getJavaClass() {
    if (javaClass == null) {
      TypeToken<J> token = new TypeToken<J>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<J> result = (Class<J>) token.getRawType();
      javaClass = result;
    }
    return javaClass;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Class<Delegator<? extends I>> getLuaClass() {
    return (Class) Delegator.class;
  }

  private @Nullable Class<I> instanceClass;

  public Class<I> getInstanceClass() {
    if (instanceClass == null) {
      TypeToken<I> token = new TypeToken<I>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<I> result = (Class<I>) token.getRawType();
      instanceClass = result;
    }
    return instanceClass;
  }

  public void registerInstanceConverter(@Resource LuaConverters converters) {
    converters
        .registerLuaToJavaConverter(new TypeTokenLuaToJavaConverter<I, Delegator<? extends I>>() {
          @Override
          public String getName() {
            return BasicLuaClass.this.getName();
          }

          @Override
          public Class<I> getJavaClass() {
            return getInstanceClass();
          }

          @Override
          public I getJavaInstance(Delegator<? extends I> luaInstance) {
            return luaInstance.getDelegate();
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
