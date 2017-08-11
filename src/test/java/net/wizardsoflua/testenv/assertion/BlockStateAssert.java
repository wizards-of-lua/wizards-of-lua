package net.wizardsoflua.testenv.assertion;

import org.assertj.core.api.Assertions;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockStateAssert extends WolAbstractObjectAssert<BlockStateAssert, IBlockState> {

  public BlockStateAssert(IBlockState actual) {
    super(actual, BlockStateAssert.class);
  }

  public BlockStateAssert isA(Block expected) {
    isNotNull();
    Assertions.assertThat(actual.getBlock()).as(description("block")).isEqualTo(expected);
    return myself;
  }

}
