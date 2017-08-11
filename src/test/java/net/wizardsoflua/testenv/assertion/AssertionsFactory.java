package net.wizardsoflua.testenv.assertion;

import org.assertj.core.api.Assertions;

import net.minecraft.block.state.IBlockState;

public class AssertionsFactory extends Assertions {

  public static BlockStateAssert assertThat(IBlockState actual) {
    return new BlockStateAssert(actual);
  }
}
