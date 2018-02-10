package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public abstract class ProxyingLuaClass<J, P extends DelegatingProxy<? extends J>>
    extends JavaLuaClass<J, P> {
  private @Nullable Class<P> proxyClass;

  public Class<P> getProxyClass() {
    if (proxyClass == null) {
      @SuppressWarnings("serial")
      TypeToken<P> token = new TypeToken<P>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<P> rawType = (Class<P>) token.getRawType();
      proxyClass = rawType;
    }
    return proxyClass;
  }

  @Override
  public J toJava(Table luaObj) throws ClassCastException {
    P proxy = getProxyClass().cast(luaObj);
    return proxy.getDelegate();
  }
}
