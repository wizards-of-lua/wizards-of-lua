package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.state.IBlockState;

public class WolBlockState {
  private IBlockState delegate;

  public WolBlockState(IBlockState delegate) {
    this.delegate = delegate;
  }

  public IBlockState getDelegate() {
    return delegate;
  }
}
