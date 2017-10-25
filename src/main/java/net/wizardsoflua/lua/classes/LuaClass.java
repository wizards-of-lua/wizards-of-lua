package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.Converters;

public abstract class LuaClass<J, L extends Table> {
  private final Map<String, LuaFunction> functions = new HashMap<>();
  private Table metatable;
  protected Converters converters;

  private @Nullable Class<J> javaClass;

  public Class<J> getJavaClass() {
    if (javaClass == null) {
      @SuppressWarnings("serial")
      TypeToken<J> token = new TypeToken<J>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<J> rawType = (Class<J>) token.getRawType();
      javaClass = rawType;
    }
    return javaClass;
  }

  public Table getMetatable() {
    return metatable;
  }

  void setMetatable(Table metatable) {
    this.metatable = checkNotNull(metatable, "metatable==null!");
    for (Map.Entry<String, LuaFunction> e : functions.entrySet()) {
      metatable.rawset(e.getKey(), e.getValue());
    }
  }

  public Converters getConverters() {
    return converters;
  }

  void setConverters(Converters converters) {
    this.converters = checkNotNull(converters, "converters==null!");
  }

  protected void add(String name, LuaFunction function) {
    functions.put(name, function);
  }

  public L getLuaInstance(J javaObj) {
    return toLua(javaObj);
  }

  public J getJavaInstance(Table luaObj) {
    checkAssignable(luaObj);
    return toJava(luaObj);
  }

  public void checkAssignable(Object luaObj) {
    getConverters().getTypes().checkAssignable(getMetatableName(), luaObj);
  }

  protected abstract String getMetatableName();

  protected abstract L toLua(J javaObj);

  protected abstract J toJava(Table luaObj);
}
