package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.BlockStateInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.block.state.IBlockState;

@LuaClass("BlockState")
public class BlockStateClass extends AbstractLuaType {
  public BlockStateInstance newInstance(IBlockState delegate) {
    return new BlockStateInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
