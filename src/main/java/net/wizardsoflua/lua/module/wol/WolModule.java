package net.wizardsoflua.lua.module.wol;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

/**
 * The Time module provides some information about the Wizards of Lua modification.
 */
@GenerateLuaModuleTable
@GenerateLuaDoc(name = WolModule.NAME, subtitle = "Some Information about the Wizards of Lua")
@AutoService(SpellExtension.class)
public class WolModule extends LuaTableExtension {
  public static final String NAME = "Wol";
  @Resource
  private LuaConverters converters;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new WolModuleTable<>(this, converters);
  }

  /**
   * The 'version' property holds the version string of the Wizards of Lua modification installed on
   * the server.
   */
  @LuaProperty
  public String getVersion() {
    return WizardsOfLua.VERSION;
  }
}
