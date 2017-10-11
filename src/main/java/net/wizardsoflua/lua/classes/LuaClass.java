package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.Converters;

public abstract class LuaClass<J> {
  private final Class<J> type;
  private final Map<String, LuaFunction> functions = new HashMap<>();
  private Converters converters;
  private Table metatable;

  public LuaClass(Class<J> type) {
    this.type = checkNotNull(type, "type==null!");;
  }

  public Class<J> getType() {
    return type;
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

  public Table getLuaInstance(J javaObj) {
    return toLua(javaObj);
  }

  protected void add(String name, LuaFunction function) {
    functions.put(name, function);
  }

  public abstract Table toLua(J javaObj);

  public abstract J toJava(Table luaObj);


}
