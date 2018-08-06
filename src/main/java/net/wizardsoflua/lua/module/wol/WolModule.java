package net.wizardsoflua.lua.module.wol;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;

/**
 * The Wol module provides some information about the Wizards of Lua modification.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = WolModule.NAME, subtitle = "Some Information about the Wizards of Lua")
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
