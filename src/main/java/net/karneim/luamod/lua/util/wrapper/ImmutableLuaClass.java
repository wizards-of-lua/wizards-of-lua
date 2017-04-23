package net.karneim.luamod.lua.util.wrapper;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.sandius.rembulan.Table;

public abstract class ImmutableLuaClass<J> extends LuaClass
    implements LuaWrapper<J, PatchedImmutableTable> {
  public ImmutableLuaClass(Table env) {
    super(env);
  }

  protected abstract void addProperties(PatchedImmutableTable.Builder builder, J javaObject);

  @Override
  public final PatchedImmutableTable toLuaObject(J javaObject) {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    addProperties(builder, javaObject);
    builder.setMetatable(getLuaClassTable());
    return builder.build();
  }
}
