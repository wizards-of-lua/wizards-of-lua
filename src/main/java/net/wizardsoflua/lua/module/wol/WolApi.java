package net.wizardsoflua.lua.module.wol;

import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.module.LuaModuleApi;

/**
 * The Time module provides some information about the Wizards of Lua modification.
 */
@GenerateLuaModule(name = "Wol")
@GenerateLuaDoc(subtitle = "Some Information about the Wizards of Lua")
public class WolApi extends LuaModuleApi<WolAdapter> {
  public WolApi(LuaClassLoader classLoader, WolAdapter delegate) {
    super(classLoader, delegate);
  }

  /**
   * The 'version' property holds the version string of the Wizards of Lua modification installed on
   * the server.
   */
  @LuaProperty
  public String getVersion() {
    return delegate.getVersion();
  }
}
