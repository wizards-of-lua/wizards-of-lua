package net.wizardsoflua.lua.extension.spi;

import javax.annotation.Nullable;

public interface LuaClass extends LuaModule {
  @Nullable
  Class<? extends LuaClass> getSuperClassType();
}
