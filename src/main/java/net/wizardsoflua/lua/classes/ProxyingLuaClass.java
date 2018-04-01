package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.LuaInstance;

public abstract class ProxyingLuaClass<J, P extends LuaInstance<? extends J>>
    extends JavaLuaClass<J, P> {
  private @Nullable Class<P> instanceClass;

  public Class<P> getInstanceClass() {
    if (instanceClass == null) {
      @SuppressWarnings("serial")
      TypeToken<P> token = new TypeToken<P>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<P> rawType = (Class<P>) token.getRawType();
      instanceClass = rawType;
    }
    return instanceClass;
  }

  @Override
  public J toJava(Table luaObj) throws ClassCastException {
    P instance = getInstanceClass().cast(luaObj);
    return instance.getDelegate();
  }
}
