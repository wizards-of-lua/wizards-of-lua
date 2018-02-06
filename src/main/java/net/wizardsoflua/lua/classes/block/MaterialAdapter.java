package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.material.Material;
import net.wizardsoflua.annotation.lua.LuaModule;
import net.wizardsoflua.annotation.lua.LuaProperty;

/**
 * The Material class describes the physical behaviour of a [Block]({% link _modules/Block.md %}).
 */
@LuaModule(subtitle = "Physical Properties of Blocks", type = "class")
public class MaterialAdapter {
  private final Material delegate;

  public MaterialAdapter(Material delegate) {
    this.delegate = delegate;
  }

  /**
   * This is true if this material is solid.
   */
  @LuaProperty
  public boolean isSolid() {
    return delegate.isSolid();
  }

  @LuaProperty
  public boolean isLiquid() {
    return delegate.isLiquid();
  }

  @LuaProperty
  public boolean isReplaceable() {
    return delegate.isReplaceable();
  }

  @LuaProperty
  public boolean isOpaque() {
    return delegate.isOpaque();
  }
}
