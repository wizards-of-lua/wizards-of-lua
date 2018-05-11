package net.wizardsoflua.lua.classes.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

public interface Delegator<D> {
  @Deprecated
  public static <D> Class<D> getDelegateClassOf(Class<? extends Delegator<D>> delegatorClass) {
    TypeToken<? extends Delegator<D>> token = TypeToken.of(delegatorClass);
    Type superType = token.getSupertype(Delegator.class).getType();
    ParameterizedType parameterizedSuperType = (ParameterizedType) superType;
    Type arg0 = parameterizedSuperType.getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<D> typeArg0 = (Class<D>) TypeToken.of(arg0).getRawType();
    return typeArg0;
  }

  D getDelegate();
}
