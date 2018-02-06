package net.wizardsoflua;

import net.wizardsoflua.annotation.lua.LuaModule;
import net.wizardsoflua.annotation.lua.LuaProperty;

/**
 * The Material class describes the physical behaviour of a [Block]({% link _modules/Block.md %}).
 */
@LuaModule(subtitle = "Physical Properties of Blocks", type = "class")
public class MaterialAdapter {
  /**
   * This is true if this material is solid.
   */
  @LuaProperty
  public boolean isSolid() {
    return false;
  }

  /**
   * Mehrere
   * Zeilen
   */
  @LuaProperty
  public CharSequence isLiquid() {
    return null;
  }

  @LuaProperty
  public void setLiquid(CharSequence liquid) {}
}
