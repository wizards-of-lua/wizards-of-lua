package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public abstract class DelegatingProxy<D> extends DelegatingTable {
  private final LuaClassLoader classLoader;
  protected D delegate;

  public DelegatingProxy(LuaClassLoader classLoader, D delegate) {
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public static <D> Class<D> getDelegateClassOf(Class<? extends DelegatingProxy<D>> proxyClass) {
    TypeToken<? extends DelegatingProxy<D>> token = TypeToken.of(proxyClass);
    Type superType = token.getSupertype(DelegatingProxy.class).getType();
    ParameterizedType parameterizedSuperType = (ParameterizedType) superType;
    Type arg0 = parameterizedSuperType.getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<D> typeArg0 = (Class<D>) TypeToken.of(arg0).getRawType();
    return typeArg0;
  }

  public Converters getConverters() {
    return classLoader.getConverters();
  }

  public void setDelegate(D delegate) {
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public D getDelegate() {
    return delegate;
  }

  public abstract boolean isTransferable();
}
