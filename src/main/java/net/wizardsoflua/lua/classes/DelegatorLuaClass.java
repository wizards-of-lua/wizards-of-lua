package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.Delegator;

public abstract class DelegatorLuaClass<J, L extends Table & Delegator<? extends J>>
    extends JavaLuaClass<J, L> {
  private @Nullable Class<L> instanceClass;

  public Class<L> getInstanceClass() {
    if (instanceClass == null) {
      @SuppressWarnings("serial")
      TypeToken<L> token = new TypeToken<L>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<L> rawType = (Class<L>) token.getRawType();
      instanceClass = rawType;
    }
    return instanceClass;
  }

  @Override
  public J toJava(Table luaObj) throws ClassCastException {
    L instance = getInstanceClass().cast(luaObj);
    return instance.getDelegate();
  }
}
